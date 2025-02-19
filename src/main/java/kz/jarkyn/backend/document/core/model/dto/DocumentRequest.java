
package kz.jarkyn.backend.document.core.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import kz.jarkyn.backend.party.model.Currency;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
@JsonDeserialize(builder = ImmutableDocumentRequest.Builder.class)
public interface DocumentRequest extends NamedDto {
    @Nullable String getName();
    IdDto getOrganization();
    LocalDateTime getMoment();
    Currency getCurrency();
    BigDecimal getExchangeRate();
    BigDecimal getAmount();
    String getComment();
}
