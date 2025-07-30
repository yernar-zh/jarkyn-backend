package kz.jarkyn.backend.audit.model.dto;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface EntityGroupChangeResponse {
    String getEntityName();
    List<EntityChangeResponse> getEntityChanges();
}
