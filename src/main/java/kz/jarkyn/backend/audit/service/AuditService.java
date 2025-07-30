package kz.jarkyn.backend.audit.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.audit.mapper.ChangeMapper;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.dto.EntityChangeResponse;
import kz.jarkyn.backend.audit.model.dto.EntityGroupChangeResponse;
import kz.jarkyn.backend.audit.model.dto.FieldChangeResponse;
import kz.jarkyn.backend.audit.model.dto.MainEntityChangeResponse;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.core.model.ReferenceEntity;
import kz.jarkyn.backend.core.model.dto.Pair;
import kz.jarkyn.backend.core.model.dto.Triple;
import kz.jarkyn.backend.core.sorts.EntitySorts;
import kz.jarkyn.backend.audit.specifications.AuditSpecifications;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.service.UserService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AuditService {
    private static final String TYPE_NAME = "$type";
    private static final String TYPE_CREATED = "CREATED";
    private static final String TYPE_EDITED = "EDITED";

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

    public List<MainEntityChangeResponse> findChanges(UUID entityId) {
        List<List<AuditEntity>> changeGroups = auditRepository.findAll(
                        Specification.where(AuditSpecifications.relatedEntityId(entityId)))
                .stream()
                .collect(Collectors.groupingBy(AuditEntity::getMoment)).entrySet()
                .stream().sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue).toList();
        Map<UUID, Map<String, String>> beforeValueMap = new HashMap<>();
        return changeGroups.stream().map(changeGroupAudits -> {
            List<Pair<String, EntityChangeResponse>> changeEntityPairs = changeGroupAudits
                    .stream().collect(Collectors.groupingBy(AuditEntity::getEntityId))
                    .values()
                    .stream().map(entityChangeAudits -> {
                        Set<String> existKeys = entityChangeAudits
                                .stream()
                                .map(AuditEntity::getName)
                                .filter(fieldName -> !fieldName.startsWith(TYPE_NAME))
                                .collect(Collectors.toSet());
                        Map<String, String> beforeFiledValues = beforeValueMap
                                .computeIfAbsent(entityChangeAudits.getFirst().getEntityId(), _ -> new HashMap<>());
                        String type = entityChangeAudits.stream()
                                .filter(audit -> !audit.getName().equals(TYPE_NAME))
                                .map(AuditEntity::getValue)
                                .findFirst().orElse(beforeValueMap.isEmpty() ? TYPE_CREATED : TYPE_EDITED);
                        List<Triple<String, String, String>> existChanges = entityChangeAudits.stream()
                                .filter(audit -> existKeys.contains(audit.getName()))
                                .map(audit -> {
                                    String beforeFiledValue = beforeFiledValues.get(audit.getName());
                                    beforeFiledValues.put(audit.getName(), audit.getValue());
                                    return Triple.of(audit.getName(), beforeFiledValue, audit.getValue());
                                }).toList();
                        List<Triple<String, String, String>> noChanges = beforeFiledValues.entrySet().stream()
                                .filter(entry -> !existKeys.contains(entry.getKey()))
                                .map(entry -> Triple.of(entry.getKey(), entry.getValue(), entry.getValue()))
                                .toList();
                        List<Triple<String, String, String>> changes = Stream
                                .of(existChanges, noChanges)
                                .flatMap(Collection::stream).toList();
                        String groupName = changes.stream()
                                .map(Triple::getFirst)
                                .map(name -> name.split("\\."))
                                .max(Comparator.comparing(parts -> parts.length))
                                .filter(parts -> parts.length > 1)
                                .map(parts -> parts[0])
                                .orElse(null);
                        List<FieldChangeResponse> changeFields = changes.stream().map(triple ->
                                changeMapper.toFieldChangeResponse(
                                        groupName != null ?
                                                triple.getFirst().substring(groupName.length()) : triple.getFirst(),
                                        toJsonNode(triple.getSecond()),
                                        toJsonNode(triple.getThird()))
                        ).toList();
                        return Pair.of(groupName, changeMapper.toEntityChangeResponse(type, changeFields));
                    }).toList();
            String type = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() == null)
                    .map(pair -> pair.getSecond().getType())
                    .findFirst().orElse(TYPE_EDITED);
            List<FieldChangeResponse> fieldChanges = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() == null)
                    .map(Pair::getSecond).map(EntityChangeResponse::getChangeFields)
                    .findFirst().orElse(List.of())
                    .stream()
                    .filter(_ -> !type.equals(TYPE_CREATED))
                    .filter(fieldChange -> !type.equals(TYPE_EDITED) ||
                                           Objects.equals(fieldChange.getAfter(), fieldChange.getBefore()))
                    .toList();
            List<EntityGroupChangeResponse> entityGroupChanges = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() != null)
                    .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> changeMapper.toEntityGroupChangeResponse(entry.getKey(), entry.getValue()))
                    .toList();
            AuditEntity firstChangeGroupAudit = changeGroupAudits.getFirst();
            return changeMapper.toMainEntityChange(
                    firstChangeGroupAudit.getMoment(), firstChangeGroupAudit.getUser(),
                    type, fieldChanges, entityGroupChanges);
        }).sorted(Comparator.comparing(MainEntityChangeResponse::getMoment).reversed()).toList();
    }

    public void saveEntity(AbstractEntity entity) {
        saveEntity(entity, entity, null);
    }

    public void saveEntity(AbstractEntity entity, AbstractEntity relatedEntity, String entityName) {
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
                save(entity, relatedEntity.getId(),
                        entityName != null ? entityName + "." + field.getName() : field.getName(),
                        field.get(entity));
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(AbstractEntity entity, AbstractEntity relatedEntity) {
        addAction(entity, relatedEntity, "DELETED");
    }

    @Transactional
    public void commit(AbstractEntity entity, AbstractEntity relatedEntity) {
        addAction(entity, relatedEntity, "DELETED");
    }

    @Transactional
    public void addAction(AbstractEntity entity, AbstractEntity relatedEntity, String action) {
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
        newAudit.setEntityName(entity.getClass().getSimpleName());
        newAudit.setEntityId(entity.getId());
        newAudit.setRelatedEntityId(relatedEntity.getId());
        newAudit.setName(TYPE_CREATED);
        newAudit.setValue(action);
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
                        .and(AuditSpecifications.name(fieldName))
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

        if (oldAudit != null && oldAudit.getValue().equals(fieldValue)) {
            return;
        }
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
        newAudit.setEntityName(entity.getClass().getSimpleName());
        newAudit.setEntityId(entity.getId());
        newAudit.setRelatedEntityId(relatedEntityId);
        newAudit.setName(fieldName);
        newAudit.setValue(fieldValue);
        auditRepository.save(newAudit);
    }
}
