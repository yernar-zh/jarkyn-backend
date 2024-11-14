
package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

@Value.Immutable
public interface ItemRequest extends IdDto {
    IdDto getGood();
    Integer getPrice();
    Integer getQuantity();
}
