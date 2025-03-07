
package kz.jarkyn.backend.warehouse.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface GoodListResponse extends ReferenceResponse {
    ReferenceResponse getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    BigDecimal getWeight();
    Boolean getArchived();
    String getAttributeIds();
    BigDecimal getSellingPrice();
    Integer getRemain();
    BigDecimal getCostPrice();
}
