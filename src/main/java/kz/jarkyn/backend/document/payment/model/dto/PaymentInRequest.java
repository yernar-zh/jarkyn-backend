
package kz.jarkyn.backend.document.payment.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutablePaymentInRequest.Builder.class)
public interface PaymentInRequest extends NamedDto {
    @Nullable String getName();
    IdDto getAccount();
    @Nullable IdDto getCounterparty();
    LocalDateTime getMoment();
    BigDecimal getAmount();
    String getComment();
    List<PaidDocumentRequest> getPaidDocuments();
}
