package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.global.model.dto.CurrencyResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface SupplierListResponse extends IdNamedDto {
    BigDecimal getAccountBalance();
    CurrencyResponse getAccountCurrency();
    @Nullable LocalDateTime getFirstSupplyMoment();
    @Nullable LocalDateTime getLastSupplyMoment();
    Integer getTotalSupplyCount();
    BigDecimal getTotalSupplyAmount();
    Boolean getArchived();
}
