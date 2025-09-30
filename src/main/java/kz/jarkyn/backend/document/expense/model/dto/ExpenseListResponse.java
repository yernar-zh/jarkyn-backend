
package kz.jarkyn.backend.document.expense.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface ExpenseListResponse extends DocumentResponse {
    @Nullable String getReceiptNumber();
    EnumTypeResponse getItemOfExpenditure();
    String getPurpose();
    BigDecimal getAttachedAmount();
    BigDecimal getNotAttachedAmount();
}
