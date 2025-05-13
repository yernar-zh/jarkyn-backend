
package kz.jarkyn.backend.document.change.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface ChargeResponse extends DocumentResponse {
    @Nullable ReferenceResponse getCounterparty();
    List<ChargeDocumentResponse> getChargeDocuments();
}
