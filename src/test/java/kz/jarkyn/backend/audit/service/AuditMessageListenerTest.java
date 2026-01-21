package kz.jarkyn.backend.audit.service;

import jakarta.persistence.EntityManager;
import kz.jarkyn.backend.audit.model.message.AuditSaveMessage;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.user.model.SessionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditMessageListenerTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditMessageListener auditMessageListener;

    @Test
    void onAuditSave_shouldCallAuditServiceWithInstantFromMessage() {
        UUID entityId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        Instant instant = Instant.parse("2026-01-21T10:00:00Z");

        AuditSaveMessage message = new AuditSaveMessage();
        message.setEntityId(entityId);
        message.setEntityClass(TestEntity.class.getName());
        message.setSessionId(sessionId);
        message.setInstant(instant);

        TestEntity entity = new TestEntity();
        when(entityManager.find(eq(TestEntity.class), eq(entityId))).thenReturn(entity);

        SessionEntity session = new SessionEntity();
        when(entityManager.find(eq(SessionEntity.class), eq(sessionId))).thenReturn(session);

        auditMessageListener.onAuditSave(message);

        verify(auditService).saveEntity(eq(entity), eq(entity), isNull(), eq(session), eq(instant));
    }

    public static class TestEntity extends AbstractEntity {
    }
}
