package kz.jarkyn.backend.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.jarkyn.backend.audit.mapper.ChangeMapper;
import kz.jarkyn.backend.audit.model.AuditEntity;
import kz.jarkyn.backend.audit.model.message.AuditSaveMessage;
import kz.jarkyn.backend.audit.repository.AuditRepository;
import kz.jarkyn.backend.core.config.AppRabbitTemplate;
import kz.jarkyn.backend.core.config.RabbitRoutingKeys;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.utils.RequestMoment;
import kz.jarkyn.backend.user.model.SessionEntity;
import kz.jarkyn.backend.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;
    @Mock
    private ChangeMapper changeMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private AuthService authService;
    @Mock
    private AppRabbitTemplate appRabbitTemplate;
    @Mock
    private ObjectProvider<RequestMoment> requestMomentProvider;

    @InjectMocks
    private AuditService auditService;

    @BeforeEach
    void setUp() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void archive_shouldUseNow_whenRequestAttributesPresentButNoRequestMoment() {
        RequestAttributes attributes = mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        when(requestMomentProvider.getIfAvailable()).thenReturn(null);

        SessionEntity session = new SessionEntity();
        session.setId(UUID.randomUUID());
        when(authService.getCurrentSession()).thenReturn(session);

        TestEntity entity = new TestEntity();
        entity.setId(UUID.randomUUID());

        Instant before = Instant.now();
        auditService.archive(entity);
        Instant after = Instant.now();

        ArgumentCaptor<AuditEntity> auditCaptor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditRepository).save(auditCaptor.capture());

        AuditEntity savedAudit = auditCaptor.getValue();
        assertFalse(savedAudit.getMoment().isBefore(before));
        assertFalse(savedAudit.getMoment().isAfter(after));
    }

    @Test
    void archive_shouldUseCurrentMoment() {
        RequestAttributes attributes = mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        Instant expected = Instant.parse("2026-01-21T10:00:00Z");
        RequestMoment requestMoment = mock(RequestMoment.class);
        when(requestMoment.getMoment()).thenReturn(expected);
        when(requestMomentProvider.getIfAvailable()).thenReturn(requestMoment);

        SessionEntity session = new SessionEntity();
        session.setId(UUID.randomUUID());
        when(authService.getCurrentSession()).thenReturn(session);

        TestEntity entity = new TestEntity();
        entity.setId(UUID.randomUUID());

        auditService.archive(entity);

        ArgumentCaptor<AuditEntity> auditCaptor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditRepository).save(auditCaptor.capture());

        AuditEntity savedAudit = auditCaptor.getValue();
        assertEquals(expected, savedAudit.getMoment());
        assertEquals(entity.getId(), savedAudit.getEntityId());
        assertEquals("ARCHIVE", savedAudit.getAction());
        assertEquals(session, savedAudit.getSession());
    }

    @Test
    void archive_shouldUseNow_whenNoRequestAttributes() {
        SessionEntity session = new SessionEntity();
        session.setId(UUID.randomUUID());
        when(authService.getCurrentSession()).thenReturn(session);

        TestEntity entity = new TestEntity();
        entity.setId(UUID.randomUUID());

        Instant before = Instant.now();
        auditService.archive(entity);
        Instant after = Instant.now();

        ArgumentCaptor<AuditEntity> auditCaptor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditRepository).save(auditCaptor.capture());

        AuditEntity savedAudit = auditCaptor.getValue();
        assertFalse(savedAudit.getMoment().isBefore(before));
        assertFalse(savedAudit.getMoment().isAfter(after));
    }

    @Test
    void saveEntity_shouldUseRequestMomentForMultipleCalls() throws Exception {
        RequestAttributes attributes = mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        Instant expected = Instant.parse("2026-01-21T10:00:00Z");
        RequestMoment requestMoment = mock(RequestMoment.class);
        when(requestMoment.getMoment()).thenReturn(expected);
        when(requestMomentProvider.getIfAvailable()).thenReturn(requestMoment);

        SessionEntity session = new SessionEntity();
        session.setId(UUID.randomUUID());
        when(authService.getCurrentSession()).thenReturn(session);

        TestEntity entity = new TestEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Test");

        when(auditRepository.findOne(any(Specification.class), any())).thenReturn(Optional.empty());
        when(objectMapper.valueToTree(any())).thenReturn(null); // Simple enough for this test
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"name\":\"Test\"}");

        // Call saveEntity twice
        auditService.saveEntity(entity);
        auditService.saveEntity(entity, entity, "related");

        ArgumentCaptor<AuditEntity> auditCaptor = ArgumentCaptor.forClass(AuditEntity.class);
        verify(auditRepository, atLeastOnce()).save(auditCaptor.capture());

        for (AuditEntity audit : auditCaptor.getAllValues()) {
            assertEquals(expected, audit.getMoment(), "All audits in same request should have same moment");
        }
    }

    @Test
    void saveEntityAsync_shouldSendRabbitMessageWithRequestMoment() {
        RequestAttributes attributes = mock(RequestAttributes.class);
        RequestContextHolder.setRequestAttributes(attributes);

        Instant expected = Instant.parse("2026-01-21T10:00:00Z");
        RequestMoment requestMoment = mock(RequestMoment.class);
        when(requestMoment.getMoment()).thenReturn(expected);
        when(requestMomentProvider.getIfAvailable()).thenReturn(requestMoment);

        SessionEntity session = new SessionEntity();
        session.setId(UUID.randomUUID());
        when(authService.getCurrentSession()).thenReturn(session);

        TestEntity entity = new TestEntity();
        entity.setId(UUID.randomUUID());

        auditService.saveEntityAsync(entity);

        ArgumentCaptor<AuditSaveMessage> messageCaptor = ArgumentCaptor.forClass(AuditSaveMessage.class);
        verify(appRabbitTemplate).sendAfterCommit(eq(RabbitRoutingKeys.AUDIT_SAVE), messageCaptor.capture());

        AuditSaveMessage message = messageCaptor.getValue();
        assertEquals(expected, message.getInstant());
        assertEquals(entity.getId(), message.getEntityId());
        assertEquals(session.getId(), message.getSessionId());
    }

    public static class TestEntity extends AbstractEntity {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
