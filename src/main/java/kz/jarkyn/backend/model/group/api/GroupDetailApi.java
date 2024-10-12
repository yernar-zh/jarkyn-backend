
package kz.jarkyn.backend.model.group.api;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface GroupDetailApi {
    UUID getId();
    String getName();
    @Nullable GroupDetailApi getParent();
}
