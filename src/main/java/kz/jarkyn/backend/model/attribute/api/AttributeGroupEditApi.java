package kz.jarkyn.backend.model.attribute.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

import java.util.List;
import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeGroupEditApi.Builder.class)
public interface AttributeGroupEditApi {
    String getName();
    List<AttributeApi> getAttributes();
}
