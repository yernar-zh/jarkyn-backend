package kz.jarkyn.backend.warehouse.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;


@Value.Immutable
public interface GroupShortResponse extends ReferenceResponse {
    String getSearchKeywords();
    @Nullable Integer getMinimumMarkup();
    @Nullable Integer getSellingMarkup();
}
