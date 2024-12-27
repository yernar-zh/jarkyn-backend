
package kz.jarkyn.backend.stock.mode.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface TurnoverResponse {
    IdDto getWarehouse();
    IdDto getGood();
    Integer getRemain();
    Integer getQuantity();
    BigDecimal getCostPrice();
}
