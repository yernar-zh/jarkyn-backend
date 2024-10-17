package kz.jarkyn.backend.model.common.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.ImmutableIdNamedApi;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableIdNamedApi.Builder.class)
public interface IdNamedDto extends IdDto {
    String getName();
}
