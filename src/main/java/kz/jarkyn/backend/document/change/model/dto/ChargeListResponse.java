
package kz.jarkyn.backend.document.change.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface ChargeListResponse extends DocumentResponse {
    ReferenceResponse getCounterparty();
    @Nullable BigDecimal getAttached();
    @Nullable BigDecimal getNotAttached();
}
