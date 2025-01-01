
package kz.jarkyn.backend.document.payment.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.payment.model.ItemOfExpenditure;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface PaymentInListResponse extends DocumentResponse {
    IdNamedDto getAccount();
    @Nullable IdNamedDto getCounterparty();
    @Nullable String getReceiptNumber();
    @Nullable BigDecimal getAttached();
    @Nullable BigDecimal getNotAttached();
}
