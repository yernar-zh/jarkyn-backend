package kz.jarkyn.backend.audit.model.dto;

import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface EntityChangeResponse {
    Type getType();
    String getEntityName();
    UUID getEntityId();
    List<FieldChangeResponse> getFieldChanges();

    enum Type {
        CREATED, EDITED, DELETED
    }
}
