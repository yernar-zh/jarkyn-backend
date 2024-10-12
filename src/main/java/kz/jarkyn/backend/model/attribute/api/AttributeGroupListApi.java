package kz.jarkyn.backend.model.attribute.api;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface AttributeGroupListApi {
    UUID getId();
    String getName();
}
