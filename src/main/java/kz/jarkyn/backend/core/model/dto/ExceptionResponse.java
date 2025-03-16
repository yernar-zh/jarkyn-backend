package kz.jarkyn.backend.core.model.dto;

import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
public interface ExceptionResponse {
    String getCode();
    String getMessage();
    @Nullable String getStacktrace();
}
