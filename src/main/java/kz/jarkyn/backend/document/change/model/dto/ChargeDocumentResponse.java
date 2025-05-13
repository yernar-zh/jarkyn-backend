
package kz.jarkyn.backend.document.change.model.dto;

import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface ChargeDocumentResponse extends IdDto {
    DocumentResponse getPayment();
    DocumentResponse getDocument();
    BigDecimal getAmount();
}
