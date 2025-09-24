
package kz.jarkyn.backend.document.payment.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PaymentOutResponse extends DocumentResponse {
    @Nullable String getReceiptNumber();
    EnumTypeResponse getItemOfExpenditure();
    @Nullable String getPurpose();
    List<PaidDocumentResponse> getPaidDocuments();
}
