
package kz.jarkyn.backend.model.good.api;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface GoodDetailApi {
    UUID getId();
    String getName();
}
