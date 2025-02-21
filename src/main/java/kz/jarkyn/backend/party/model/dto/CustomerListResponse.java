package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.global.model.dto.CurrencyResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface CustomerListResponse extends IdNamedDto {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
    BigDecimal getAccountBalance();
    CurrencyResponse getAccountCurrency();
    @Nullable LocalDateTime getFirstSaleMoment();
    @Nullable LocalDateTime getLastSaleMoment();
    Integer getTotalSaleCount();
    BigDecimal getTotalSaleAmount();
    Boolean getArchived();
}
