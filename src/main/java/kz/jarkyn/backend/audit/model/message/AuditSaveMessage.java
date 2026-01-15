package kz.jarkyn.backend.audit.model.message;

import java.time.Instant;
import java.util.UUID;

public class AuditSaveMessage {
    private UUID entityId;
    private String entityClass;
    private UUID relatedEntityId;
    private String relatedEntityClass;
    private String entityName;
    private UUID sessionId;
    private Instant instant;

    public AuditSaveMessage() {
    }

    public AuditSaveMessage(
            UUID entityId,
            String entityClass,
            UUID relatedEntityId,
            String relatedEntityClass,
            String entityName,
            UUID sessionId,
            Instant instant) {
        this.entityId = entityId;
        this.entityClass = entityClass;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityClass = relatedEntityClass;
        this.entityName = entityName;
        this.sessionId = sessionId;
        this.instant = instant;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    public UUID getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(UUID relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public String getRelatedEntityClass() {
        return relatedEntityClass;
    }

    public void setRelatedEntityClass(String relatedEntityClass) {
        this.relatedEntityClass = relatedEntityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }
}
