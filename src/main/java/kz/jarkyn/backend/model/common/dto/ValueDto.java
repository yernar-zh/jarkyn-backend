package kz.jarkyn.backend.model.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableValueDto.Builder.class)
public interface ValueDto<T> {
    T getValue();
}
