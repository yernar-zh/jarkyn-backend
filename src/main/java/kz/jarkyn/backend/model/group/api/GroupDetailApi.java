
package kz.jarkyn.backend.model.group.api;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface GroupDetailApi {
    UUID getId();
    String getName();
    @Nullable IdNamedApi getParent();
    List<IdNamedApi> getChildren();
}
