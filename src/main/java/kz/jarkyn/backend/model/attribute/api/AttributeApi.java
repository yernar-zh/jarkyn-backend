package kz.jarkyn.backend.model.attribute.api;

import kz.jarkyn.backend.model.common.api.IdApi;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface AttributeApi extends IdApi {
    String getName();
}
