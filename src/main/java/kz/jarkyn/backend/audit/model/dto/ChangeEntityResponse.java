package kz.jarkyn.backend.audit.model.dto;

import kz.jarkyn.backend.audit.model.AuditType;
import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface ChangeEntityResponse {
    AuditType getType();
    String getEntityName();
    UUID getEntityId();
    List<ChangeFieldResponse> getFieldChanges();
}
