package kz.jarkyn.backend.counterparty.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.counterparty.model.Currency;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface AccountResponse extends IdNamedDto {
    String getBank();
    String getGiro();
    Currency getCurrency();
    BigDecimal getBalance();
}
