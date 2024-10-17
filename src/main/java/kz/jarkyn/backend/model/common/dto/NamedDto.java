package kz.jarkyn.backend.model.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.ImmutableNamedApi;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableNamedApi.Builder.class)
public interface NamedDto {
    String getName();
}
