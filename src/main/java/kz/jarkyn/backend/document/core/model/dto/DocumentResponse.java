
package kz.jarkyn.backend.document.core.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value.Immutable
public interface DocumentResponse {
    UUID getId();
    EnumTypeResponse getType();
    String getName();
    ReferenceResponse getOrganization();
    @Nullable ReferenceResponse getCounterparty();
    @Nullable ReferenceResponse getWarehouse();
    @Nullable ReferenceResponse getAccount();
    Instant getMoment();
    EnumTypeResponse getCurrency();
    BigDecimal getExchangeRate();
    BigDecimal getAmount();
    Boolean getDeleted();
    Boolean getCommited();
    String getComment();
}
