
package kz.jarkyn.backend.document.payment.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentRequest;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutablePaymentOutRequest.Builder.class)
public interface PaymentOutRequest extends DocumentRequest {
    IdDto getAccount();
    @Nullable IdDto getCounterparty();
    @Nullable String getReceiptNumber();
    IdDto getItemOfExpenditure();
    @Nullable String getPurpose();
    List<PaidDocumentRequest> getPaidDocuments();
}
