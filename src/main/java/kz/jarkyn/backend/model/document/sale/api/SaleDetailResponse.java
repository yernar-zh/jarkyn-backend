
package kz.jarkyn.backend.model.document.sale.api;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import kz.jarkyn.backend.model.document.common.api.DocumentResponse;
import kz.jarkyn.backend.model.document.common.api.ItemResponse;
import kz.jarkyn.backend.model.document.payment.api.PaymentInForSaleResponse;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value.Immutable
public interface SaleDetailResponse extends DocumentResponse {
    IdNamedDto getWarehouse();
    IdNamedDto getCustomer();
    String getComment();
    @Nullable LocalDateTime getShipmentMoment();
    List<ItemResponse> getItems();
    List<PaymentInForSaleResponse> getPayments();
}
