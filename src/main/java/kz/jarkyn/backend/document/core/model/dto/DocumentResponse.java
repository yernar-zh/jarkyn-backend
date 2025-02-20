
package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.reference.model.dto.CurrencyResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface DocumentResponse extends IdNamedDto {
    IdNamedDto getOrganization();
    LocalDateTime getMoment();
    IdNamedDto getCurrency();
    BigDecimal getExchangeRate();
    BigDecimal getAmount();
    Boolean getDeleted();
    Boolean getCommited();
    String getComment();
}
