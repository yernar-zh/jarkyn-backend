package kz.jarkyn.backend.audit.service;

import jakarta.persistence.EntityManager;
import kz.jarkyn.backend.audit.model.message.AuditSaveMessage;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.user.model.SessionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditMessageListener {
    private static final Logger log = LoggerFactory.getLogger(AuditMessageListener.class);

    private final EntityManager entityManager;
    private final AuditService auditService;

    public AuditMessageListener(EntityManager entityManager, AuditService auditService) {
        this.entityManager = entityManager;
        this.auditService = auditService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.audit-save}", concurrency = "4")
    public void onAuditSave(AuditSaveMessage msg) {
        try {
            AbstractEntity entity = load(msg.getEntityClass(), msg.getEntityId());
            if (entity == null) return; // nothing to do
            AbstractEntity related = msg.getRelatedEntityClass() != null && msg.getRelatedEntityId() != null
                    ? load(msg.getRelatedEntityClass(), msg.getRelatedEntityId())
                    : entity;
            if (related == null) related = entity;
            SessionEntity session = entityManager.find(SessionEntity.class, msg.getSessionId());
            if (session == null) {
                log.error("AUDIT_LISTENER: Session not found: {}", msg.getSessionId());
                return;
            }
            auditService.saveEntity(entity, related, msg.getEntityName(), session, msg.getInstant());
        } catch (Exception e) {
            log.error("AUDIT_LISTENER", e);
        }
    }

    private AbstractEntity load(String className, UUID id) {
        if (className == null || id == null) return null;
        try {
            Class<?> entityClass = Class.forName(className);
            Object obj = entityManager.find(entityClass, id);
            return (obj instanceof AbstractEntity) ? (AbstractEntity) obj : null;
        } catch (ClassNotFoundException e) {
            log.error("AUDIT_LISTENER: Class not found {}", className, e);
            return null;
        }
    }
}
