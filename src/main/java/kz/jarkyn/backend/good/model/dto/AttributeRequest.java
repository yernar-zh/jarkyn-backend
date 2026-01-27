package kz.jarkyn.backend.good.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableAttributeRequest.Builder.class)
public interface AttributeRequest extends ReferenceRequest {
    IdDto getGroup();
    String getSearchKeywords();
}
