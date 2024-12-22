package kz.jarkyn.backend.counterparty.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.counterparty.model.Currency;
import kz.jarkyn.backend.counterparty.model.OrganizationEntity;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface AccountResponse extends IdNamedDto {
    IdNamedDto getOrganization();
    @Nullable IdNamedDto getCounterparty();
    String getBank();
    String getGiro();
    Currency getCurrency();
    BigDecimal getBalance();
}
