package kz.jarkyn.backend.model.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.ImmutableIdApi;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableIdApi.Builder.class)
public interface IdDto {
    UUID getId();
}
