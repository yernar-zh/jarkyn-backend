package kz.jarkyn.backend.party.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.global.model.dto.CurrencyResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface AccountResponse extends IdNamedDto {
    IdNamedDto getOrganization();
    @Nullable IdNamedDto getCounterparty();
    String getBank();
    String getGiro();
    CurrencyResponse getCurrency();
    BigDecimal getBalance();
}
