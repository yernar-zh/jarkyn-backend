package kz.jarkyn.backend.document.sale.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.sale.model.SaleState;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface SaleListResponse extends DocumentResponse {
    IdNamedDto getWarehouse();
    IdNamedDto getCounterparty();
    @Nullable LocalDateTime getShipmentMoment();
    SaleState getState();
    BigDecimal getPaidAmount();
    BigDecimal getNotPaidAmount();
    BigDecimal getCostPrice();
    BigDecimal getProfit();
}
