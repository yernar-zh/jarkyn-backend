
package kz.jarkyn.backend.document.expense.model.dto;

import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface ExpensesListResponse extends DocumentResponse {
    BigDecimal getAttachedAmount();
    BigDecimal getNotAttachedAmount();
}
