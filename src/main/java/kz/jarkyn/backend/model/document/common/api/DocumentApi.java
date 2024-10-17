
package kz.jarkyn.backend.model.document.common.api;

import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

import java.time.LocalDateTime;

@Value.Immutable
public interface DocumentApi extends IdNamedDto {
    IdNamedDto getWarehouse();
    IdNamedDto getCustomer();
    LocalDateTime getMoment();
    String getState();
    Integer getAmount();
    String getComment();
}
