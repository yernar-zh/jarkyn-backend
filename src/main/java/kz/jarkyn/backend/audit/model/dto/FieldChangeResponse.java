package kz.jarkyn.backend.audit.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public interface FieldChangeResponse {
    String getFieldName();
    @Nullable JsonNode getBefore();
    @Nullable JsonNode getAfter();
}
