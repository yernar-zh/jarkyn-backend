package kz.jarkyn.backend.document.sale.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.sale.model.SaleState;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value.Immutable
public interface SaleListResponse extends DocumentResponse {
    ReferenceResponse getWarehouse();
    ReferenceResponse getCounterparty();
    @Nullable Instant getShipmentMoment();
    SaleState getState();
    BigDecimal getPaidAmount();
    BigDecimal getNotPaidAmount();
    BigDecimal getCostPrice();
    BigDecimal getProfit();
}
