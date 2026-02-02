package kz.jarkyn.backend.global.model.dto;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface BulkResponse {
    int getTotal();
    int getSuccess();
    int getFailed();
    List<BulkErrorResponse> getErrors();
}
