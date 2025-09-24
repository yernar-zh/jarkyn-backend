
package kz.jarkyn.backend.document.expense.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kz.jarkyn.backend.document.core.model.dto.DocumentRequest;
import kz.jarkyn.backend.document.payment.model.dto.ImmutablePaymentOutRequest;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutablePaymentOutRequest.Builder.class)
public interface ExpensesRequest extends DocumentRequest {
    List<RelatedExpensesRequest> getPaidDocuments();
}
