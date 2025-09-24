
package kz.jarkyn.backend.document.expense.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.payment.model.dto.ImmutablePaidDocumentRequest;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutablePaidDocumentRequest.Builder.class)
public interface RelatedExpenseRequest extends IdDto {
    @Nullable UUID getId();
    IdDto getDocument();
    BigDecimal getAmount();
}
