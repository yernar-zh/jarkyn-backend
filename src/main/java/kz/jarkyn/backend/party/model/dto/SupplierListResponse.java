package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface SupplierListResponse extends ReferenceResponse {
    BigDecimal getAccountBalance();
    ReferenceResponse getAccountCurrency();
    @Nullable LocalDateTime getFirstSupplyMoment();
    @Nullable LocalDateTime getLastSupplyMoment();
    Integer getTotalSupplyCount();
    BigDecimal getTotalSupplyAmount();
    Boolean getArchived();
}
