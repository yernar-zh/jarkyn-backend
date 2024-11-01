
package kz.jarkyn.backend.document.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

@Value.Immutable
public interface ItemResponse extends IdDto {
    GoodItemResponse getGood();
    Integer getPrice();
    Integer getQuantity();
}
