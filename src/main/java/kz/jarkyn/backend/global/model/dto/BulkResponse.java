package kz.jarkyn.backend.global.model.dto;

import kz.jarkyn.backend.core.model.dto.ExceptionResponse;
import org.immutables.value.Value;
import org.springframework.lang.Nullable;

@Value.Immutable
public interface BulkResponse<T> {
    boolean isSuccess();
    @Nullable T getData();
    @Nullable ExceptionResponse getError();
}
