
package kz.jarkyn.backend.document.expense.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface ExpenseResponse extends DocumentResponse {
    @Nullable String getReceiptNumber();
    EnumTypeResponse getItemOfExpenditure();
    List<BindDocumentResponse> getBindDocuments();
    List<BindDocumentResponse> getPaidDocuments();

}
