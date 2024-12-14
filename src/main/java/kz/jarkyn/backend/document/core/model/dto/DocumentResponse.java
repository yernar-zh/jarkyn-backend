
package kz.jarkyn.backend.document.core.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value.Immutable
public interface DocumentResponse extends IdNamedDto {
    LocalDateTime getMoment();
    BigDecimal getAmount();
    Boolean getDeleted();
}
