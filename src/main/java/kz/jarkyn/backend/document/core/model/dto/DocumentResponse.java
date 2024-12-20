
package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.counterparty.model.Currency;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface DocumentResponse extends IdNamedDto {
    LocalDateTime getMoment();
    Currency getCurrency();
    BigDecimal getExchangeRate();
    BigDecimal getAmount();
    Boolean getDeleted();
}
