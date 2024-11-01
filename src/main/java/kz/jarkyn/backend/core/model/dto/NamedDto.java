package kz.jarkyn.backend.core.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableNamedDto.Builder.class)
public interface NamedDto {
    String getName();
}
