
package kz.jarkyn.backend.document.expense.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value.Immutable
//@JsonDeserialize(builder = ImmutableRelatedExpenseRequest.Builder.class)
public interface RelatedExpenseRequest extends IdDto {
    @Nullable UUID getId();
    IdDto getDocument();
    BigDecimal getAmount();
}
