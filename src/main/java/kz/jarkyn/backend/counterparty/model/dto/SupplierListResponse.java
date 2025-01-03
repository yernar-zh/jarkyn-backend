package kz.jarkyn.backend.counterparty.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.counterparty.model.Currency;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface SupplierListResponse extends IdNamedDto {
    BigDecimal getAccountBalance();
    Currency getAccountCurrency();
    @Nullable LocalDateTime getFirstSupplyMoment();
    @Nullable LocalDateTime getLastSupplyMoment();
    Integer getTotalSupplyCount();
    BigDecimal getTotalSupplyAmount();
}
