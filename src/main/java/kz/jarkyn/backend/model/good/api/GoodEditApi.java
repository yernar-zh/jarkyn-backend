package kz.jarkyn.backend.model.good.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.api.IdApi;
import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGoodEditApi.Builder.class)
public interface GoodEditApi {
    UUID getId();
    String getName();
    @Nullable IdApi getGroup();
    @Nullable IdApi getImage();
    Integer getMinimumPrice();
    List<IdApi> getTransports();
}
