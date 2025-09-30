
package kz.jarkyn.backend.document.expense.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.bind.model.dto.BindDocumentRequest;
import kz.jarkyn.backend.document.core.model.dto.DocumentRequest;
import kz.jarkyn.backend.document.payment.model.dto.ImmutablePaymentOutRequest;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableExpenseRequest.Builder.class)
public interface ExpenseRequest extends DocumentRequest {
    IdDto getAccount();
    @Nullable String getReceiptNumber();
    IdDto getItemOfExpenditure();
    @Nullable String getPurpose();
    List<BindDocumentRequest> getBindDocuments();
}
