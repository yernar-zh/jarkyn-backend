
package kz.jarkyn.backend.model.document.sale.api;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import kz.jarkyn.backend.model.document.common.ItemEntity;
import kz.jarkyn.backend.model.document.common.api.DocumentApi;
import kz.jarkyn.backend.model.document.common.api.ItemApi;
import kz.jarkyn.backend.model.document.payment.api.PaymentPurposeApi;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value.Immutable
public interface SaleDetailApi extends DocumentApi {
    LocalDateTime getShipmentMoment();
    Integer getPlacesQuantity();
    Integer getItemsTotalAmount();
    Integer getDiscountAmount();
    List<ItemApi> items();
    List<PaymentPurposeApi> paymentPurposes();
}
