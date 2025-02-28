
package kz.jarkyn.backend.document.payment.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface PaymentInListResponse extends DocumentResponse {
    ReferenceResponse getAccount();
    @Nullable ReferenceResponse getCounterparty();
    @Nullable String getReceiptNumber();
    @Nullable BigDecimal getAttached();
    @Nullable BigDecimal getNotAttached();
}
