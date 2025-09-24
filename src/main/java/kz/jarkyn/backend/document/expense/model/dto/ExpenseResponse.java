
package kz.jarkyn.backend.document.expense.model.dto;

import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.expense.model.RelatedExpenseEntity;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface ExpenseResponse extends DocumentResponse {
    List<RelatedExpenseEntity> getRelatedExpenses();
}
