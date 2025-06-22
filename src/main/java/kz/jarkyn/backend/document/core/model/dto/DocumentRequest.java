
package kz.jarkyn.backend.document.core.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value.Immutable
@JsonDeserialize(builder = ImmutableDocumentRequest.Builder.class)
public interface DocumentRequest {
    String getName();
    IdDto getOrganization();
    Instant getMoment();
    IdDto getCurrency();
    BigDecimal getExchangeRate();
    BigDecimal getAmount();
    String getComment();
}
