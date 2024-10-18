package kz.jarkyn.backend.model.attribute.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeEditRequest.Builder.class)
public interface AttributeEditRequest extends NamedDto {
}
