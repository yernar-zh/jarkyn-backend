package kz.jarkyn.backend.warehouse.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeEditRequest.Builder.class)
public interface AttributeEditRequest extends NamedDto {
}
