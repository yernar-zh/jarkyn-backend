package kz.jarkyn.backend.model.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableIdNamedApi.Builder.class)
public interface IdNamedApi extends IdApi {
    String getName();
}
