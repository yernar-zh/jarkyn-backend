
package kz.jarkyn.backend.good.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupDetailResponse extends ReferenceResponse {
    String getSearchKeywords();
    @Nullable Integer getMinimumMarkup();
    @Nullable Integer getSellingMarkup();
    @Nullable ReferenceResponse getParent();
    List<GroupShortResponse> getChildren();
}
