
package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Value.Immutable
public interface DocumentResponse {
    UUID getId();
    String getName();
    ReferenceResponse getOrganization();
    LocalDateTime getMoment();
    ReferenceResponse getCurrency();
    BigDecimal getExchangeRate();
    BigDecimal getAmount();
    Boolean getDeleted();
    Boolean getCommited();
    String getComment();
}
