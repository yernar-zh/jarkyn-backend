
package kz.jarkyn.backend.stock.mode.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface StockResponse {
    IdNamedDto getWarehouse();
    IdNamedDto getGood();
    Integer getRemain();
    BigDecimal getCostPrice();
}
