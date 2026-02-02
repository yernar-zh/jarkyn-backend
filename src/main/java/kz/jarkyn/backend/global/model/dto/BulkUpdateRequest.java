package kz.jarkyn.backend.global.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableBulkUpdateRequest.Builder.class)
public interface BulkUpdateRequest<T> extends IdDto {
    T getRequest();
}
