package kz.jarkyn.backend.warehouse.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupResponse extends ReferenceResponse {
    String getSearchKeywords();
    @Nullable Integer getMinimumMarkup();
    @Nullable Integer getSellingMarkup();
    List<GroupResponse> getChildren();
}
