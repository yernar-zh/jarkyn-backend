
package kz.jarkyn.backend.document.payment.model.dto;

import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface PaymentInForSaleResponse {
    DocumentResponse getPaymentIn();
    DocumentResponse getSale();
    Integer getAmount();
}
