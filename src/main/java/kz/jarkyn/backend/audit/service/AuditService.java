package kz.jarkyn.backend.audit.service;


import kz.jarkyn.backend.audit.config.IgnoreAudit;
import kz.jarkyn.backend.audit.config.ParentAudit;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.core.model.AbstractEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional
    public <E extends AbstractEntity> void saveChanges(E entity) {
        Field[] fields = entity.getClass().getDeclaredFields();
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
            if (!oldAudit.getEntityParentId().equals(entityParentId)) {
                throw new RuntimeException();
            }
            if (oldAudit.getFieldValue().equals(fieldValue)) {
                return;
            }
        }
        UUID userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuditEntity newAudit = new AuditEntity();
        newAudit.setUserId(Objects.requireNonNull(userId));
        newAudit.setMoment(LocalDateTime.now());
        newAudit.setEntityId(entityId);
        newAudit.setEntityParentId(entityParentId);
        newAudit.setFieldName(fieldName);
        newAudit.setFieldValue(fieldValue);
        auditRepository.save(newAudit);
    }
}
