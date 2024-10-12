package kz.jarkyn.backend.model.attribute.api;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface AttributeGroupDetailApi extends IdNamedApi {
    List<AttributeDetailApi> getAttributes();
}
