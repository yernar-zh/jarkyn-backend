package kz.jarkyn.backend.audit.model.dto;

import kz.jarkyn.backend.audit.model.AuditType;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;

@Value.Immutable
public interface ChangeGroupResponse {
    Instant getMoment();
    ReferenceResponse getUser();
    AuditType getType();
    List<EntityChangeResponse> getEntityChanges();
}
