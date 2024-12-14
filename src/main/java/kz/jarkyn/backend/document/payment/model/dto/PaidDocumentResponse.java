
package kz.jarkyn.backend.document.payment.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface PaidDocumentResponse extends IdDto {
    DocumentResponse getPayment();
    DocumentResponse getDocument();
    BigDecimal getAmount();
}
