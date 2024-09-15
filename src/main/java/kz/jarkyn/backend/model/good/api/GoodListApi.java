
package kz.jarkyn.backend.model.good.api;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface GoodListApi {
    UUID getId();
    String getName();
    @Nullable IdNamedApi getGroup();
    @Nullable IdApi getImage();
    Integer getMinimumPrice();
    List<IdNamedApi> getTransports();
}
