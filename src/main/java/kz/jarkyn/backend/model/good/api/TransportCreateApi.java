package kz.jarkyn.backend.model.good.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.api.IdApi;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableTransportCreateApi.Builder.class)
public interface TransportCreateApi {
    String getName();
    @Nullable IdApi getParent();
    Integer getPosition();
}
