package kz.jarkyn.backend.model.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableIdNamedDto.Builder.class)
public interface IdNamedDto extends IdDto {
    String getName();
}
