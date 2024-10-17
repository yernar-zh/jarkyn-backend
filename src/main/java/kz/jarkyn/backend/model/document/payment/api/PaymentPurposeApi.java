
package kz.jarkyn.backend.model.document.payment.api;

import kz.jarkyn.backend.model.document.common.api.DocumentApi;
import org.immutables.value.Value;

@Value.Immutable
public interface PaymentPurposeApi {
    DocumentApi getPayment();
    DocumentApi getDocument();
    Integer partAmount();
}
