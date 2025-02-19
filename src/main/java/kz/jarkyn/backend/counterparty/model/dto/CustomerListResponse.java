package kz.jarkyn.backend.counterparty.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.counterparty.model.Currency;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface CustomerListResponse extends IdNamedDto {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
    BigDecimal getAccountBalance();
    Currency getAccountCurrency();
    @Nullable LocalDateTime getFirstSaleMoment();
    @Nullable LocalDateTime getLastSaleMoment();
    Integer getTotalSaleCount();
    BigDecimal getTotalSaleAmount();
    Boolean getArchived();
}
