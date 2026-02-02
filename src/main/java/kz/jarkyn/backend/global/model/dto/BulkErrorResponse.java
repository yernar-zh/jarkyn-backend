package kz.jarkyn.backend.global.model.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface BulkErrorResponse {
    int getIndex();
    String getMessage();
}
