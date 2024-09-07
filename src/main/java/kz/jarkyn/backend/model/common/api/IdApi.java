package kz.jarkyn.backend.model.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableIdApi.Builder.class)
public interface IdApi {
    UUID getId();
}
