
package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface ItemResponse extends IdDto {
    GoodItemResponse getGood();
    BigDecimal getPrice();
    Integer getQuantity();
    Integer getRemain();
    BigDecimal getCostPrice();
}
