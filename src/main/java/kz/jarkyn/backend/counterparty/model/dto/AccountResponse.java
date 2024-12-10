package kz.jarkyn.backend.counterparty.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface AccountResponse extends IdNamedDto {
    BigDecimal getBalance();
    String getBank();
    String getGiro();
}
