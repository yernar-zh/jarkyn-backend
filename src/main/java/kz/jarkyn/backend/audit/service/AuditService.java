package kz.jarkyn.backend.audit.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.audit.mapper.ChangeMapper;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.AuditType;
import kz.jarkyn.backend.audit.model.dto.ChangeEntityResponse;
import kz.jarkyn.backend.audit.model.dto.ChangeGroupResponse;
import kz.jarkyn.backend.audit.model.dto.ChangeFieldResponse;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.core.model.ReferenceEntity;
import kz.jarkyn.backend.core.sorts.EntitySorts;
import kz.jarkyn.backend.audit.specifications.AuditSpecifications;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.service.UserService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuditService {
    private final AuditRepository auditRepository;
    private final UserService userService;
    private final ChangeMapper changeMapper;
    private final ObjectMapper objectMapper;

    public AuditService(
            AuditRepository auditRepository,
            UserService userService,
            ChangeMapper changeMapper,
            ObjectMapper objectMapper) {
        this.auditRepository = auditRepository;
        this.userService = userService;
        this.changeMapper = changeMapper;
        this.objectMapper = objectMapper;
    }

    public List<ChangeGroupResponse> findChanges(UUID entityId) {
        List<List<AuditEntity>> changeGroups = auditRepository.findAll(
                        Specification.where(AuditSpecifications.relatedEntityId(entityId)))
                .stream().collect(Collectors.groupingBy(
                        AuditEntity::getMoment,
                        Collectors.collectingAndThen(
                                Collectors.toMap(
                                        audit -> Pair.of(audit.getEntityId(), audit.getFieldName()),
                                        audit -> audit, (a1, a2) -> a1.getCreatedAt().isAfter(a2.getCreatedAt()) ? a1 : a2
                                ),
                                map -> List.copyOf(map.values())
                        ))).values().stream()
                .sorted(Comparator.comparing(changeGroupAudits -> changeGroupAudits.getFirst().getMoment())).toList();

        Map<UUID, Map<String, String>> beforeFiledValuesMap = new HashMap<>();
        final Boolean[] created = {true};
        return changeGroups.stream().map(changeGroupAudits -> {
            List<ChangeEntityResponse> entityChanges = changeGroupAudits
                    .stream().collect(Collectors.groupingBy(AuditEntity::getEntityId))
                    .values().stream().map(entityChangeAudits -> {
                        AuditEntity firstEntityChangeAudit = entityChangeAudits.getFirst();
                        Set<String> existKeys = entityChangeAudits.stream().map(AuditEntity::getFieldName)
                                .filter(fieldName -> !fieldName.isEmpty()).collect(Collectors.toSet());
                        Map<String, String> beforeFiledValues = beforeFiledValuesMap
                                .computeIfAbsent(entityChangeAudits.getFirst().getEntityId(), _ -> new HashMap<>());
                        List<ChangeFieldResponse> existChanges = entityChangeAudits.stream()
                                .filter(audit -> !audit.getFieldName().isEmpty()).map(audit -> {
                                    String beforeFiledValue = beforeFiledValues.get(audit.getFieldName());
                                    beforeFiledValues.put(audit.getFieldName(), audit.getFieldValue());
                                    return changeMapper.toFieldChangeResponse(audit.getFieldName(),
                                            toJsonNode(beforeFiledValue),
                                            toJsonNode(audit.getFieldValue()));
                                }).toList();
                        List<ChangeFieldResponse> noChanges = beforeFiledValues.entrySet().stream()
                                .filter(entry -> !existKeys.contains(entry.getKey()))
                                .map(entry -> changeMapper.toFieldChangeResponse(entry.getKey(),
                                        toJsonNode(entry.getValue()),
                                        toJsonNode(entry.getValue())))
                                .toList();
                        return changeMapper.toEntityChangeResponse(
                                firstEntityChangeAudit.getType(),
                                firstEntityChangeAudit.getEntityName(), firstEntityChangeAudit.getEntityId(),
                                Stream.of(existChanges, noChanges).flatMap(Collection::stream).toList());
                    }).toList();
            AuditEntity firstChangeGroupAudit = changeGroupAudits.getFirst();
            if (created[0]) {
                created[0] = false;
                return changeMapper.toGroupResponse(
                        firstChangeGroupAudit.getMoment(), firstChangeGroupAudit.getUser(),
                        ChangeGroupResponse.Type.CREATED, List.of());
            } else {
                return changeMapper.toGroupResponse(
                        firstChangeGroupAudit.getMoment(), firstChangeGroupAudit.getUser(),
                        ChangeGroupResponse.Type.EDITED, entityChanges);
            }

        }).sorted(Comparator.comparing(ChangeGroupResponse::getMoment).reversed()).toList();
    }

    public void saveChanges(AbstractEntity entity) {
        saveChanges(entity, entity);
    }

    public void saveChanges(AbstractEntity entity, AbstractEntity relatedEntity) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = entity.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(IgnoreAudit.class)) {
                    continue;
                }
                field.setAccessible(true);
                save(entity, relatedEntity.getId(), field.getName(), field.get(entity));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteChanges(AbstractEntity entity, AbstractEntity relatedEntity) {
        UserEntity user = userService.findCurrent();
        Instant moment = auditRepository.findAll(Specification
                        .where(AuditSpecifications.relatedEntityId(relatedEntity.getId()))
                        .and(AuditSpecifications.user(user))
                        .and(AuditSpecifications.createdLessThanOneSecond()))
                .stream().map(AuditEntity::getMoment)
                .findAny().orElse(Instant.now());
        AuditEntity newAudit = new AuditEntity();
        newAudit.setMoment(moment);
        newAudit.setUser(user);
        newAudit.setType(AuditType.DELETED);
        newAudit.setEntityName(entity.getClass().getSimpleName());
        newAudit.setEntityId(entity.getId());
        newAudit.setRelatedEntityId(relatedEntity.getId());
        newAudit.setFieldName("");
        newAudit.setFieldValue("");
        auditRepository.save(newAudit);
    }

    private JsonNode toJsonNode(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void save(AbstractEntity entity, UUID relatedEntityId, String fieldName, Object fieldValueObj) {
        AuditEntity oldAudit = auditRepository.findOne(Specification
                        .where(AuditSpecifications.entityId(entity.getId()))
                        .and(AuditSpecifications.relatedEntityId(relatedEntityId))
                        .and(AuditSpecifications.fieldName(fieldName))
                , EntitySorts.byCreatedDesc());

        JsonNode fieldValueNode;
        if (fieldValueObj instanceof ReferenceEntity reference) {
            fieldValueNode = objectMapper.createObjectNode()
                    .put("id", String.valueOf(reference.getId()))
                    .put("name", reference.getName())
                    .put("archived", reference.getArchived());
        } else if (fieldValueObj instanceof DocumentEntity document) {
            fieldValueNode = objectMapper.createObjectNode()
                    .put("id", String.valueOf(document.getId()))
                    .put("name", document.getName())
                    .put("deleted", document.getDeleted());
        } else {
            fieldValueNode = objectMapper.valueToTree(fieldValueObj);
        }

        String fieldValue;
        try {
            fieldValue = objectMapper.writeValueAsString(fieldValueNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (oldAudit != null && oldAudit.getFieldValue().equals(fieldValue)) {
            return;
        }
        AuditType type;
        if (oldAudit == null) type = AuditType.CREATED;
        else type = AuditType.EDITED;

        UserEntity user = userService.findCurrent();
        Instant moment = auditRepository.findAll(Specification
                        .where(AuditSpecifications.relatedEntityId(relatedEntityId))
                        .and(AuditSpecifications.user(user))
                        .and(AuditSpecifications.createdLessThanOneSecond()))
                .stream().map(AuditEntity::getMoment)
                .findAny().orElse(Instant.now());
        AuditEntity newAudit = new AuditEntity();
        newAudit.setMoment(moment);
        newAudit.setUser(user);
        newAudit.setType(type);
        newAudit.setEntityName(entity.getClass().getSimpleName());
        newAudit.setEntityId(entity.getId());
        newAudit.setRelatedEntityId(relatedEntityId);
        newAudit.setFieldName(fieldName);
        newAudit.setFieldValue(fieldValue);
        auditRepository.save(newAudit);
    }
}
