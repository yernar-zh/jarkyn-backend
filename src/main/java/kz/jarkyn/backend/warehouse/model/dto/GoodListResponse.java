
package kz.jarkyn.backend.warehouse.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.global.model.dto.ImageResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface GoodListResponse extends ReferenceResponse {
    GroupShortResponse getGroup();
    @Nullable ImageResponse getImage();
    Integer getMinimumPrice();
    BigDecimal getWeight();
    Boolean getArchived();
    String getPath();
    String getGroupIds();
    String getAttributeIds();
    BigDecimal getSellingPrice();
    Integer getRemain();
    BigDecimal getCostPrice();
}
