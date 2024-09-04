
package kz.jarkyn.backend.model.good.api;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface GroupDetailApi {
    UUID getId();
    String getName();
    GroupDetailApi getParent();
}
