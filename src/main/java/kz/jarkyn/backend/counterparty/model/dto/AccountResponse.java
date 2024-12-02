package kz.jarkyn.backend.counterparty.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.counterparty.model.CounterpartyEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface AccountResponse extends IdNamedDto {
    Integer getBalance();
    String getBank();
    String getGiro();
}
