
package kz.jarkyn.backend.model.document.payment.api;

import kz.jarkyn.backend.model.document.common.api.DocumentResponse;
import org.immutables.value.Value;

@Value.Immutable
public interface PaymentInForSaleResponse {
    DocumentResponse getPaymentIn();
    DocumentResponse getSale();
    Integer getAmount();
}
