
package kz.jarkyn.backend.model.group.api;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupDetailApi extends IdNamedApi {
    @Nullable IdNamedApi getParent();
    List<IdNamedApi> getChildren();
}
