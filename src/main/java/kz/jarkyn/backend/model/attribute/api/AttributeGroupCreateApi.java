package kz.jarkyn.backend.model.attribute.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.api.NamedApi;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeGroupCreateApi.Builder.class)
public interface AttributeGroupCreateApi extends NamedApi {
}
