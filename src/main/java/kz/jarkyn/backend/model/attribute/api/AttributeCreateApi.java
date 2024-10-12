package kz.jarkyn.backend.model.attribute.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.common.api.IdNamedApi;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeCreateApi.Builder.class)
public interface AttributeCreateApi extends IdNamedApi {
    IdApi getGroup();
}
