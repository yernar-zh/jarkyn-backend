package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface AccountResponse extends ReferenceResponse {
    ReferenceResponse getOrganization();
    @Nullable ReferenceResponse getCounterparty();
    String getBank();
    String getGiro();
    ReferenceResponse getCurrency();
    BigDecimal getBalance();
}
