
package kz.jarkyn.backend.model.good.api;

import jakarta.annotation.Nullable;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface TransportDetailApi {
    UUID getId();
    String getName();
    @Nullable TransportDetailApi getParent();
    Integer getPosition();
}
