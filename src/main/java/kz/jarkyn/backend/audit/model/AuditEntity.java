package kz.jarkyn.backend.audit.model;

import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;

import java.util.UUID;

@Entity
@Table(name = "audit")
public class AuditEntity extends AbstractEntity {
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "entity_name")
    private String entityName;
    @Column(name = "entity_id")
    private UUID entityId;
    @Column(name = "entity_parent_id")
    private UUID entityParentId;
    @Column(name = "field_name")
    private String fieldName;
    @Column(name = "field_value")
    private String fieldValue;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityParentId() {
        return entityParentId;
    }

    public void setEntityParentId(UUID entityParentId) {
        this.entityParentId = entityParentId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}