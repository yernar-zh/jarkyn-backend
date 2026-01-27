package kz.jarkyn.backend.good.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface AttributeResponse extends ReferenceResponse {
    AttributeGroupResponse getGroup();
    String getSearchKeywords();
}
