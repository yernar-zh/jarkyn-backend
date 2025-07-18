package kz.jarkyn.backend.audit.service;


import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.audit.config.ParentAudit;
import kz.jarkyn.backend.audit.mapper.ChangeMapper;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.dto.ChangeGroupResponse;
import kz.jarkyn.backend.audit.model.dto.ChangeResponse;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.audit.sorts.AuditSorts;
import kz.jarkyn.backend.audit.specifications.AuditSpecifications;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.service.UserService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collector;
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
        Map<Instant, List<AuditEntity>> mainMap = auditRepository.findAll(
                        Specification.where(AuditSpecifications.entityId(entityId)))
                .stream().collect(getAuditEntityMapCollector());
        Map<Instant, List<AuditEntity>> subMap = auditRepository.findAll(
                        Specification.where(AuditSpecifications.parentEntityId(entityId)))
                .stream().collect(getAuditEntityMapCollector());

        UserEntity user = userService.findCurrent();
        Map<UUID, Map<String, String>> beforeFiledValuesMap = new HashMap<>();
        return Stream.of(mainMap.keySet(), subMap.keySet())
                .flatMap(Collection::stream).distinct().sorted()
                .map(moment -> {
                    ChangeGroupResponse.Type type = beforeFiledValuesMap.isEmpty() ?
                            ChangeGroupResponse.Type.CREATED : ChangeGroupResponse.Type.EDITED;
                    List<ChangeResponse> mainChanges = mainMap.getOrDefault(moment, List.of())
                            .stream().map(audit -> {
                                Map<String, String> beforeFiledValues = beforeFiledValuesMap
                                        .computeIfAbsent(audit.getEntityId(), _ -> new HashMap<>());
                                String beforeFiledValue = beforeFiledValues.get(audit.getFieldName());
                                beforeFiledValues.put(audit.getFieldName(), audit.getFieldValue());
                                return changeMapper.toResponse(audit.getFieldName(), beforeFiledValue, audit.getFieldValue());
                            }).toList();
                    List<ChangeGroupResponse> subGroupChanges = subMap.getOrDefault(moment, List.of())
                            .stream().collect(Collectors.groupingBy(AuditEntity::getEntityId))
                            .values().stream().map(audits -> {
                                Set<String> existKeys = audits.stream().map(AuditEntity::getFieldName)
                                        .filter(fieldName -> !fieldName.isEmpty()).collect(Collectors.toSet());
                                Map<String, String> beforeFiledValues = beforeFiledValuesMap
                                        .computeIfAbsent(audits.getFirst().getEntityId(), _ -> new HashMap<>());
                                ChangeGroupResponse.Type subType;
                                if (beforeFiledValues.isEmpty()) {
                                    subType = ChangeGroupResponse.Type.CREATED;
                                } else if (!existKeys.isEmpty()) {
                                    subType = ChangeGroupResponse.Type.EDITED;
                                } else {
                                    subType = ChangeGroupResponse.Type.DELETED;
                                }
                                List<ChangeResponse> subChanges = audits.stream()
                                        .filter(audit -> !audit.getFieldName().isEmpty()).map(audit -> {
                                            String beforeFiledValue = beforeFiledValues.get(audit.getFieldName());
                                            beforeFiledValues.put(audit.getFieldName(), audit.getFieldValue());
                                            return changeMapper.toResponse(audit.getFieldName(), beforeFiledValue, audit.getFieldValue());
                                        }).toList();
                                List<ChangeResponse> noChanges = beforeFiledValues.entrySet().stream()
                                        .filter(entry -> !existKeys.contains(entry.getKey()))
                                        .map(entry -> changeMapper.toResponse(entry.getKey(), entry.getValue(), entry.getValue()))
                                        .toList();
                                return changeMapper.toGroupResponse(moment, user, audits.getFirst().getEntityName(), subType,
                                        Stream.of(subChanges, noChanges).flatMap(Collection::stream).toList(), null);
                            }).toList();
                    String entityName = mainMap.entrySet().stream().findFirst().orElseThrow().getValue().getFirst().getEntityName();
                    return changeMapper.toGroupResponse(
                            moment, user, entityName, type,
                            type.equals(ChangeGroupResponse.Type.CREATED) ? List.of() : mainChanges,
                            type.equals(ChangeGroupResponse.Type.CREATED) ? List.of() : subGroupChanges);
                }).sorted(Comparator.comparing(ChangeGroupResponse::getMoment).reversed()).toList();
    }


    private Collector<AuditEntity, ?, Map<Instant, List<AuditEntity>>> getAuditEntityMapCollector() {
        return Collectors.groupingBy(
                audit -> audit.getCreatedAt().truncatedTo(ChronoUnit.MINUTES),
                Collectors.collectingAndThen(
                        Collectors.toMap(
                                audit -> Pair.of(audit.getEntityId(), audit.getFieldName()),
                                audit -> audit, (a1, a2) -> a1.getCreatedAt().isAfter(a2.getCreatedAt()) ? a1 : a2
                        ),
                        map -> List.copyOf(map.values())
                )
        );
    }

    private static List<ChangeDto> buildChange(Map<Instant, List<AuditEntity>> map) {
        Map<String, String> previewsFiledValueMap = new HashMap<>();
        List<ChangeDto> result = new ArrayList<>();
        for (Map.Entry<Instant, List<AuditEntity>> entry : new TreeMap<>(map).entrySet()) {
            Map<String, Pair<String, String>> fields = new HashMap<>();
            for (AuditEntity audit : entry.getValue()) {
                if (audit.getFieldName().isEmpty()) break;
                String previewsFiledValueKey = audit.getId() + "/" + audit.getFieldName();
                String previews = previewsFiledValueMap.get(previewsFiledValueKey);
                previewsFiledValueMap.put(previewsFiledValueKey, audit.getFieldValue());
                if (previews != null) {
                    fields.put(audit.getFieldName(), Pair.of(previews, audit.getFieldValue()));
                }
            }
            result.add(new ChangeDto(entry.getKey(), Collections.unmodifiableMap(fields)));
        }
        return result;
    }

    public <E extends AbstractEntity> void saveChanges(E entity) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = entity.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        try {
            UUID entityParentId = null;
            for (Field field : fields) {
                if (!field.isAnnotationPresent(ParentAudit.class)) {
                    continue;
                }
                field.setAccessible(true);
                AbstractEntity parent = (AbstractEntity) field.get(entity);
                entityParentId = parent.getId();
                break;
            }
            for (Field field : fields) {
                if (field.isAnnotationPresent(IgnoreAudit.class)) {
                    continue;
                }
                if (field.isAnnotationPresent(ParentAudit.class)) {
                    continue;
                }
                if (field.getName().equals("id")) {
                    continue;
                }
                field.setAccessible(true);
                String fieldName = field.getName();
                String fieldValue = fieldValueToString(field.get(entity));
                save(entity.getClass().getSimpleName(), entity.getId(), entityParentId, fieldName, fieldValue);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public <E extends AbstractEntity> void deleteChanges(E entity) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = entity.getClass(); clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        try {
            UUID entityParentId = null;
            for (Field field : fields) {
                if (!field.isAnnotationPresent(ParentAudit.class)) {
                    continue;
                }
                field.setAccessible(true);
                AbstractEntity parent = (AbstractEntity) field.get(entity);
                entityParentId = parent.getId();
                break;
            }
            save(entity.getClass().getSimpleName(), entity.getId(), entityParentId, "", "");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String fieldValueToString(Object fieldValue) {
        if (fieldValue == null) {
            return "";
        } else if (fieldValue instanceof AbstractEntity abstractEntity) {
            return abstractEntity.getId().toString();
        } else {
            return fieldValue.toString();
        }
    }

    private void save(String entityName, UUID entityId, UUID entityParentId, String fieldName, String fieldValue) {
        AuditEntity oldAudit = auditRepository.findOne(Specification
                        .where(AuditSpecifications.entityId(entityId))
                        .and(AuditSpecifications.fieldName(fieldName))
                , AuditSorts.byCreatedDesc());
        if (oldAudit != null) {
            if (!Objects.equals(oldAudit.getEntityParentId(), entityParentId)) {
                throw new RuntimeException();
            }
            if (oldAudit.getFieldValue().equals(fieldValue)) {
                return;
            }
        }
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuditEntity newAudit = new AuditEntity();
        newAudit.setUserId(Objects.requireNonNull(userId));
        newAudit.setEntityName(entityName);
        newAudit.setEntityId(entityId);
        newAudit.setEntityParentId(entityParentId);
        newAudit.setFieldName(fieldName);
        newAudit.setFieldValue(fieldValue);
        auditRepository.save(newAudit);
    }

    private Map<Instant, List<AuditEntity>> groupByMinuteAndDeduplicate(List<AuditEntity> audits) {
        return audits.stream()
                .collect(Collectors.groupingBy(audit -> audit.getCreatedAt().truncatedTo(ChronoUnit.MINUTES)))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> new ArrayList<>(entry.getValue().stream()
                        .collect(Collectors.toMap(
                                audit -> audit.getEntityName() + "|" + audit.getEntityId() + "|" + audit.getFieldName(),
                                audit -> audit,
                                (a1, a2) -> a1.getCreatedAt().isAfter(a2.getCreatedAt()) ? a1 : a2
                        )).values())));
    }

    public static class ChangeDto {
        private final Instant moment;
        private final Map<String, Pair<String, String>> fields;

        public ChangeDto(Instant moment, Map<String, Pair<String, String>> fields) {
            this.moment = moment;
            this.fields = fields;
        }

        public Instant getMoment() {
            return moment;
        }

        public Map<String, Pair<String, String>> getFields() {
            return fields;
        }
    }
}
