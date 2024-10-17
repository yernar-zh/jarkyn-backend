package kz.jarkyn.backend.model.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableIdDto.Builder.class)
public interface IdDto {
    UUID getId();
}
