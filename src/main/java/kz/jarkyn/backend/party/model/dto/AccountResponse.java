package kz.jarkyn.backend.party.model.dto;

import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface AccountResponse extends ReferenceResponse {
    ReferenceResponse getOrganization();
    String getBank();
    String getGiro();
    EnumTypeResponse getCurrency();
    BigDecimal getBalance();
}
