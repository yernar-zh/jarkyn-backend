
package kz.jarkyn.backend.document.change.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentRequest;
import kz.jarkyn.backend.document.payment.model.dto.ImmutablePaymentInRequest;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutablePaymentInRequest.Builder.class)
public interface ChargeRequest extends DocumentRequest {
    IdDto getCounterparty();
    List<ChargeDocumentRequest> getChargeDocuments();
}

