package kz.jarkyn.backend.audit.service;


import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.audit.mapper.ChangeMapper;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.AuditType;
import kz.jarkyn.backend.audit.model.dto.ChangeGroupResponse;
import kz.jarkyn.backend.audit.model.dto.EntityChangeResponse;
import kz.jarkyn.backend.audit.model.dto.FieldChangeResponse;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.core.sorts.EntitySorts;
import kz.jarkyn.backend.audit.specifications.AuditSpecifications;
import kz.jarkyn.backend.core.model.AbstractEntity;
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

    public AuditService(AuditRepository auditRepository,
                        UserService userService,
                        ChangeMapper changeMapper) {
        this.auditRepository = auditRepository;
        this.userService = userService;
        this.changeMapper = changeMapper;
    }


    public List<ChangeGroupResponse> findChanges(UUID entityId) {
        Map<UUID, Map<String, String>> beforeFiledValuesMap = new HashMap<>();
        return auditRepository.findAll(
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
                .sorted(Comparator.comparing(changeGroupAudits -> changeGroupAudits.getFirst().getMoment()))
                .map(changeGroupAudits -> {
                    AuditEntity firstChangeGroupAudit = changeGroupAudits.getFirst();
                    List<EntityChangeResponse> entityChanges = changeGroupAudits
                            .stream().collect(Collectors.groupingBy(AuditEntity::getEntityId))
                            .values().stream().map(entityChangeAudits -> {
                                AuditEntity firstEntityChangeAudits = entityChangeAudits.getFirst();
                                Set<String> existKeys = entityChangeAudits.stream().map(AuditEntity::getFieldName)
                                        .filter(fieldName -> !fieldName.isEmpty()).collect(Collectors.toSet());
                                Map<String, String> beforeFiledValues = beforeFiledValuesMap
                                        .computeIfAbsent(entityChangeAudits.getFirst().getEntityId(), _ -> new HashMap<>());
                                List<FieldChangeResponse> existChanges = entityChangeAudits.stream()
                                        .filter(audit -> !audit.getFieldName().isEmpty()).map(audit -> {
                                            String beforeFiledValue = beforeFiledValues.get(audit.getFieldName());
                                            beforeFiledValues.put(audit.getFieldName(), audit.getFieldValue());
                                            return changeMapper.toFieldChangeResponse(audit.getFieldName(), beforeFiledValue, audit.getFieldValue());
                                        }).toList();
                                List<FieldChangeResponse> noChanges = beforeFiledValues.entrySet().stream()
                                        .filter(entry -> !existKeys.contains(entry.getKey()))
                                        .map(entry -> changeMapper.toFieldChangeResponse(entry.getKey(), entry.getValue(), entry.getValue()))
                                        .toList();
                                EntityChangeResponse.Type type = switch (firstEntityChangeAudits.getType()) {
                                    case CREATED -> EntityChangeResponse.Type.CREATED;
                                    case EDITED -> EntityChangeResponse.Type.EDITED;
                                    case DELETED -> EntityChangeResponse.Type.DELETED;
                                    default -> throw new IllegalStateException();
                                };
                                return changeMapper.toEntityChangeResponse(
                                        type, firstEntityChangeAudits.getEntityName(), firstEntityChangeAudits.getEntityId(),
                                        Stream.of(existChanges, noChanges).flatMap(Collection::stream).toList());
                            }).toList();
                    return changeMapper.toGroupResponse(
                            firstChangeGroupAudit.getMoment(), firstChangeGroupAudit.getUser(),
                            firstChangeGroupAudit.getType(),
                            firstChangeGroupAudit.getType().equals(AuditType.CREATED) ? List.of() : entityChanges
                    );
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
                Object fieldValueObj = field.get(entity);
                String fieldValue;
                if (fieldValueObj == null) {
                    fieldValue = "";
                } else if (fieldValueObj instanceof AbstractEntity abstractEntity) {
                    fieldValue = Objects.requireNonNull(abstractEntity.getId()).toString();
                } else {
                    fieldValue = fieldValueObj.toString();
                }
                save(entity.getClass().getSimpleName(), entity.getId(), relatedEntity.getId(),
                        field.getName(), fieldValue);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void deleteChanges(AbstractEntity entity, AbstractEntity relatedEntity) {
        AuditEntity newAudit = new AuditEntity();
        newAudit.setMoment(Instant.now());
        newAudit.setUser(userService.findCurrent());
        newAudit.setType(AuditType.DELETED);
        newAudit.setEntityName(entity.getClass().getSimpleName());
        newAudit.setEntityId(entity.getId());
        newAudit.setRelatedEntityId(relatedEntity.getId());
        newAudit.setFieldName("");
        newAudit.setFieldValue("");
        auditRepository.save(newAudit);
    }

    private void save(String entityName, UUID entityId, UUID relatedEntityId, String fieldName, String fieldValue) {
        AuditEntity oldAudit = auditRepository.findOne(Specification
                        .where(AuditSpecifications.entityId(entityId))
                        .and(AuditSpecifications.relatedEntityId(relatedEntityId))
                        .and(AuditSpecifications.fieldName(fieldName))
                , EntitySorts.byCreatedDesc());
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
        newAudit.setEntityName(entityName);
        newAudit.setEntityId(entityId);
        newAudit.setRelatedEntityId(relatedEntityId);
        newAudit.setFieldName(fieldName);
        newAudit.setFieldValue(fieldValue);
        auditRepository.save(newAudit);
    }
}
