package kz.jarkyn.backend.model.good.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.IdApi;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGroupEditApi.Builder.class)
public interface TransportEditApi {
    String getName();
    IdApi getParent();
    Integer getPosition();
}
