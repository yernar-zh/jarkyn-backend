package kz.jarkyn.backend.global.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

@Value.Immutable
public interface CurrencyResponse extends IdNamedDto {
    String getCode();
    String getSymbol();
}
