package kz.jarkyn.backend.audit.model;

import jakarta.persistence.*;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.user.model.SessionEntity;
import kz.jarkyn.backend.user.model.UserEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit")
public class AuditEntity extends AbstractEntity {
    private Instant moment;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private SessionEntity session;
    @Column(name = "related_entity_id")
    private UUID relatedEntityId;
    @Column(name = "entity_id")
    private UUID entityId;
    @Column(name = "action")
    private String action;
    @Column(name = "field_name")
    private String fieldName;
    @Column(name = "field_value")
    private String fieldValue;

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public SessionEntity getSession() {
        return session;
    }

    public void setSession(SessionEntity session) {
        this.session = session;
    }

    public UUID getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(UUID relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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