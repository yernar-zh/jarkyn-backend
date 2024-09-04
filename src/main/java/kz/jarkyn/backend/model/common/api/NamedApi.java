package kz.jarkyn.backend.model.common.api;

import org.immutables.value.Value;

public interface NamedApi extends IdApi {
    String getName();
}
