package kz.jarkyn.backend.model.good.api;

import kz.jarkyn.backend.model.common.api.IdApi;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface GroupEditApi {
    UUID getId();
    String getName();
    IdApi getParent();
}
