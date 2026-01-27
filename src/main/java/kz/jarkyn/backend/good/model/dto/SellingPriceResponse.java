
package kz.jarkyn.backend.good.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface SellingPriceResponse extends IdDto {
    Integer getQuantity();
    BigDecimal getValue();
}
