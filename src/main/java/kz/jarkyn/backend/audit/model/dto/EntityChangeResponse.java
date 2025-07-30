package kz.jarkyn.backend.audit.model.dto;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface EntityChangeResponse {
    String getType();
    List<FieldChangeResponse> getChangeFields();
}
