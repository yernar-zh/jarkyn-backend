package kz.jarkyn.backend.model.common.api;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface IdApi {
    UUID getId();
}
