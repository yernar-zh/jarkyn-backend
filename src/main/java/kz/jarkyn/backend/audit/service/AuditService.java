package kz.jarkyn.backend.audit.service;


import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.audit.config.ParentAudit;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.core.model.AbstractEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

@Service
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional
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
                field.setAccessible(true);
                String fieldName = field.getName();
                String fieldValue = fieldValueToString(field.get(entity));
                save(entity.getId(), entityParentId, fieldName, fieldValue);
            }
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

    private void save(UUID entityId, UUID entityParentId, String fieldName, String fieldValue) {
        AuditEntity oldAudit = auditRepository.getLastByEntityIdAndFieldName(entityId, fieldName);
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
        newAudit.setMoment(Instant.now());
        newAudit.setEntityId(entityId);
        newAudit.setEntityParentId(entityParentId);
        newAudit.setFieldName(fieldName);
        newAudit.setFieldValue(fieldValue);
        auditRepository.save(newAudit);
    }
}
