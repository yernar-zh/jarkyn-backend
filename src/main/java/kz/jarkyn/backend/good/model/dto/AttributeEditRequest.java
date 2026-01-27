package kz.jarkyn.backend.good.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeEditRequest.Builder.class)
public interface AttributeEditRequest extends ReferenceRequest {
}
