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
import kz.jarkyn.backend.core.exception.ExceptionUtils;
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
    private static final String CREATE = "CREATE";
    private static final String EDITE = "EDITE";

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

    public MainEntityChangeResponse findLastChange(UUID entityId) {
        AuditEntity audit = auditRepository.findOne(
                        Specification.where(AuditSpecifications.relatedEntityId(entityId)),
                        EntitySorts.byCreatedDesc())
                .orElseThrow(ExceptionUtils.entityNotFound());

        return changeMapper.toMainEntityChange(
                audit.getMoment(), audit.getUser(),
                audit.getAction(), List.of(), List.of());
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
                                .map(AuditEntity::getFieldName)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());
                        Map<String, String> beforeFiledValues = beforeValueMap
                                .computeIfAbsent(entityChangeAudits.getFirst().getEntityId(), _ -> new HashMap<>());
                        String action = entityChangeAudits.stream()
                                .map(AuditEntity::getAction)
                                .findFirst().orElseThrow();
                        // fieldName, beforeFieldValue, afterFieldValue
                        List<Triple<String, String, String>> existChanges = entityChangeAudits
                                .stream()
                                .filter(audit -> existKeys.contains(audit.getFieldName()))
                                .map(audit -> {
                                    String beforeFiledValue = beforeFiledValues.get(audit.getFieldName());
                                    beforeFiledValues.put(audit.getFieldName(), audit.getFieldValue());
                                    return Triple.of(audit.getFieldName(), beforeFiledValue, audit.getFieldValue());
                                }).toList();
                        // fieldName, beforeFieldValue, afterFieldValue
                        List<Triple<String, String, String>> noChanges = beforeFiledValues.entrySet().stream()
                                .filter(entry -> !existKeys.contains(entry.getKey()))
                                .map(entry -> Triple.of(entry.getKey(), entry.getValue(), entry.getValue()))
                                .toList();
                        // fieldName, beforeFieldValue, afterFieldValue
                        List<Triple<String, String, String>> changes = Stream
                                .of(existChanges, noChanges)
                                .flatMap(Collection::stream).toList();
                        String entityName = changes.stream()
                                .map(Triple::getFirst)
                                .map(name -> name.split("\\."))
                                .max(Comparator.comparing(parts -> parts.length))
                                .filter(parts -> parts.length > 1)
                                .map(parts -> parts[0])
                                .orElse(null);
                        List<FieldChangeResponse> fieldChanges = changes.stream().map(triple ->
                                changeMapper.toFieldChangeResponse(
                                        entityName != null ? triple.getFirst().substring(entityName.length() + 1) : triple.getFirst(),
                                        toJsonNode(triple.getSecond()),
                                        toJsonNode(triple.getThird()))
                        ).toList();
                        return Pair.of(entityName, changeMapper.toEntityChangeResponse(action, fieldChanges));
                    }).toList();
            String action = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() == null)
                    .map(pair -> pair.getSecond().getAction())
                    .findFirst().orElse(EDITE);
            List<FieldChangeResponse> fieldChanges = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() == null)
                    .map(Pair::getSecond).map(EntityChangeResponse::getFieldChanges)
                    .findFirst().orElse(List.of())
                    .stream()
                    .filter(_ -> action.equals(EDITE))
                    .filter(fieldChange -> !Objects.equals(fieldChange.getBefore(), fieldChange.getAfter()))
                    .toList();
            List<EntityGroupChangeResponse> entityGroupChanges = changeEntityPairs.stream()
                    .filter(pair -> pair.getFirst() != null)
                    .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())))
                    .entrySet().stream()
                    .map(entry -> changeMapper.toEntityGroupChangeResponse(entry.getKey(), entry.getValue()))
                    .filter(_ -> action.equals(EDITE))
                    .toList();
            AuditEntity firstChangeGroupAudit = changeGroupAudits.getFirst();
            return changeMapper.toMainEntityChange(
                    firstChangeGroupAudit.getMoment(), firstChangeGroupAudit.getUser(),
                    action, fieldChanges, entityGroupChanges);
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
    public void delete(AbstractEntity entity, AbstractEntity relatedEntity, String entityName) {
        addAction(entity, relatedEntity, entityName, "DELETE");
    }

    @Transactional
    public void commit(AbstractEntity entity) {
        addAction(entity, entity, null, "COMMIT");
    }

    @Transactional
    public void undoCommit(AbstractEntity entity) {
        addAction(entity, entity, null, "UNDO_COMMIT");
    }

    @Transactional
    public void addAction(AbstractEntity entity, AbstractEntity relatedEntity, String entityName, String action) {
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
        newAudit.setEntityName(entityName);
        newAudit.setAction(action);
        newAudit.setFieldName(null);
        newAudit.setFieldName(null);
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
                , EntitySorts.byCreatedDesc()).orElse(null);

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
        newAudit.setAction(oldAudit == null ? CREATE : EDITE);
        newAudit.setFieldName(fieldName);
        newAudit.setFieldValue(fieldValue);
        auditRepository.save(newAudit);
    }
}
