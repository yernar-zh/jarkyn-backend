package kz.jarkyn.backend.model.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Value.Immutable
public interface ExceptionApi {
    String getCode();
    String getMessage();
    @Nullable Object getDetail();
    @Nullable String getStacktrace();
}
