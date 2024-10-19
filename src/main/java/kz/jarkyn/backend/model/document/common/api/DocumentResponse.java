
package kz.jarkyn.backend.model.document.common.api;

import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

import java.time.LocalDateTime;

@Value.Immutable
public interface DocumentResponse extends IdNamedDto {
    LocalDateTime getMoment();
    Integer getAmount();
}
