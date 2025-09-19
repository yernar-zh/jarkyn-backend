package kz.jarkyn.backend.warehouse.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeGroupRequest.Builder.class)
public interface AttributeGroupRequest extends ReferenceRequest {
}
