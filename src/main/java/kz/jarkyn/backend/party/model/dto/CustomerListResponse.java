package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface CustomerListResponse extends ReferenceResponse {
    String getPhoneNumber();
    String getShippingAddress();
    Integer getDiscount();
    BigDecimal getAccountBalance();
    ReferenceResponse getAccountCurrency();
    @Nullable LocalDateTime getFirstSaleMoment();
    @Nullable LocalDateTime getLastSaleMoment();
    Integer getTotalSaleCount();
    BigDecimal getTotalSaleAmount();
}
