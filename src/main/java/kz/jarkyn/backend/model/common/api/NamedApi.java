package kz.jarkyn.backend.model.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableNamedApi.Builder.class)
public interface NamedApi {
    String getName();
}
