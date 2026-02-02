package kz.jarkyn.backend.global.model.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface BulkError {
    int getIndex();
    String getMessage();
}
