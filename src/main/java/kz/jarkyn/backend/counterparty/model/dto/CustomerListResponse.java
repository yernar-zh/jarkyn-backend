package kz.jarkyn.backend.counterparty.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface CustomerListResponse extends IdNamedDto {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
    BigDecimal getBalance();
    LocalDateTime getFirstSaleMoment();
    LocalDateTime getLastSaleMoment();
    Integer getTotalSaleCount();
    Integer getTotalSaleAmount();
}
