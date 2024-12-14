
package kz.jarkyn.backend.document.payment.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface PaymentInDetailResponse extends DocumentResponse {
    @Nullable IdNamedDto getCounterparty();
    String getComment();
    List<PaidDocumentResponse> getPaidDocuments();
}
