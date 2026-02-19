package kz.jarkyn.backend.core.model.dto;

import tools.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableReferenceResponse.Builder.class)
public interface ReferenceRequest {
    String getName();
}
