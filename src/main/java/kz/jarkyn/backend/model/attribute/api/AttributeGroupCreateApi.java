package kz.jarkyn.backend.model.attribute.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import kz.jarkyn.backend.model.common.api.NamedApi;
import kz.jarkyn.backend.model.good.api.ImmutableGroupCreateApi;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeGroupCreateApi.Builder.class)
public interface AttributeGroupCreateApi {
    String getName();
}
