
package kz.jarkyn.backend.payment.model.dto;

import kz.jarkyn.backend.document.model.dto.DocumentResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface PaymentInForSaleResponse {
    DocumentResponse getPaymentIn();
    DocumentResponse getSale();
    Integer getAmount();
}
