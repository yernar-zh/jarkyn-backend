
package kz.jarkyn.backend.document.payment.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.global.model.dto.ItemOfExpenditureResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PaymentOutResponse extends DocumentResponse {
    ReferenceResponse getAccount();
    @Nullable ReferenceResponse getCounterparty();
    @Nullable String getReceiptNumber();
    ItemOfExpenditureResponse getItemOfExpenditure();
    @Nullable String getPurpose();
    List<PaidDocumentResponse> getPaidDocuments();
}
