
package kz.jarkyn.backend.model.document.common.api;

import kz.jarkyn.backend.model.common.dto.IdDto;
import org.immutables.value.Value;

@Value.Immutable
public interface ItemRequest extends IdDto {
    IdDto getGood();
    Integer getPrice();
    Integer getQuantity();
}
