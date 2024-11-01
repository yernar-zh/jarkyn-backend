
package kz.jarkyn.backend.good.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

@Value.Immutable
public interface SellingPriceDto extends IdDto {
    Integer getQuantity();
    Integer getValue();
}
