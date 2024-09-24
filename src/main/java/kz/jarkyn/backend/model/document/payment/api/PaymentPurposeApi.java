
package kz.jarkyn.backend.model.document.payment.api;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import kz.jarkyn.backend.model.document.common.DocumentEntity;
import kz.jarkyn.backend.model.document.common.ItemEntity;
import kz.jarkyn.backend.model.document.common.api.DocumentApi;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface PaymentPurposeApi {
    DocumentApi getPayment();
    DocumentApi getDocument();
    Integer partAmount();
}
