package kz.jarkyn.backend.model.attribute.api;

import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface AttributeGroupDetailApi {
    UUID getId();
    String getName();
    List<AttributeApi> getAttributes();
}
