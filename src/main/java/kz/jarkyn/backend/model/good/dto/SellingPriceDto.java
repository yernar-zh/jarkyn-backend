
package kz.jarkyn.backend.model.good.dto;

import kz.jarkyn.backend.model.common.dto.IdDto;
import org.immutables.value.Value;

@Value.Immutable
public interface SellingPriceDto extends IdDto {
    Integer getQuantity();
    Integer getValue();
}
