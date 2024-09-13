package kz.jarkyn.backend.model.good.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.ImageEntity;
import kz.jarkyn.backend.model.common.api.IdApi;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGoodCreateApi.Builder.class)
public interface GoodCreateApi {
    String getName();
    @Nullable IdApi getGroup();
    @Nullable IdApi getImage();
    Integer getMinimumPrice();
    List<IdApi> getTransports();
}
