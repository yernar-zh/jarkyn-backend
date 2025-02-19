package kz.jarkyn.backend.warehouse.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeRequest.Builder.class)
public interface AttributeRequest extends NamedDto {
    IdDto getGroup();
}
