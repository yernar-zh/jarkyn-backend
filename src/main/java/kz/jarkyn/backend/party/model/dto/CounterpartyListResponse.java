package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value.Immutable
public interface CounterpartyListResponse extends ReferenceResponse {
    @Nullable String getPhoneNumber();
    Integer getDiscount();
    String getShippingAddress();
    BigDecimal getAccountBalance();
    EnumTypeResponse getAccountCurrency();
}
