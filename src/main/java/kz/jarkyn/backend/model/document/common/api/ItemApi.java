
package kz.jarkyn.backend.model.document.common.api;

import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

@Value.Immutable
public interface ItemApi {
    IdNamedDto getGood();
    Integer getPrice();
    Integer getQuantity();
    Integer getStockQuantity();
}
