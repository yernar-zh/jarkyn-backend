package kz.jarkyn.backend.audit.model.dto;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

@Value.Immutable
public interface FieldChangeResponse {
    String getFieldName();
    @Nullable String getBefore();
    @Nullable String getAfter();
}
