package kz.jarkyn.backend.model.attribute.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeGroupCreateApi.Builder.class)
public interface AttributeGroupCreateApi {
    String getName();
}
