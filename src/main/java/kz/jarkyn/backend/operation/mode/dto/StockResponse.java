
package kz.jarkyn.backend.operation.mode.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface StockResponse {
    ReferenceResponse getWarehouse();
    ReferenceResponse getGood();
    Integer getRemain();
    BigDecimal getCostPrice();
}
