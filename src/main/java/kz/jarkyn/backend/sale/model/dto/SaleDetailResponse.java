
package kz.jarkyn.backend.sale.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.document.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.model.dto.ItemResponse;
import kz.jarkyn.backend.payment.model.dto.PaymentInForSaleResponse;
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
