package kz.jarkyn.backend.model.good.api;

import kz.jarkyn.backend.model.common.api.IdApi;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupCreateApi {
    String getName();
    IdApi getParent();
}
