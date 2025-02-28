
package kz.jarkyn.backend.document.core.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
@JsonDeserialize(builder = ImmutableDocumentRequest.Builder.class)
public interface DocumentRequest {
    @Nullable String getName();
    IdDto getOrganization();
    LocalDateTime getMoment();
    IdDto getCurrency();
    BigDecimal getExchangeRate();
    BigDecimal getAmount();
    String getComment();
}
