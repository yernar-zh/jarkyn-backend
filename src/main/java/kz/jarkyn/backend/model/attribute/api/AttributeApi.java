package kz.jarkyn.backend.model.attribute.api;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface AttributeApi {
    UUID getId();
    String getName();
}
