
package kz.jarkyn.backend.document.payment.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.global.model.dto.ItemOfExpenditureResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface PaymentOutListResponse extends DocumentResponse {
    ReferenceResponse getAccount();
    @Nullable ReferenceResponse getCounterparty();
    @Nullable String getReceiptNumber();
    ItemOfExpenditureResponse getItemOfExpenditure();
    @Nullable String getPurpose();
    @Nullable BigDecimal getAttachedAmount();
    @Nullable BigDecimal getNotAttachedAmount();
}
