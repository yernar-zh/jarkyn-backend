
package kz.jarkyn.backend.model.document.common.api;

import kz.jarkyn.backend.model.common.dto.IdDto;
import org.immutables.value.Value;

@Value.Immutable
public interface ItemResponse extends IdDto {
    GoodItemResponse getGood();
    Integer getPrice();
    Integer getQuantity();
}
