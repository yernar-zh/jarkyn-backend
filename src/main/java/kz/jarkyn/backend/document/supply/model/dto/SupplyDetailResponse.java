
package kz.jarkyn.backend.document.supply.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaymentPurposeResponse;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value.Immutable
public interface SupplyDetailResponse extends DocumentResponse {
    IdNamedDto getWarehouse();
    IdNamedDto getCustomer();
    String getComment();
    @Nullable LocalDateTime getShipmentMoment();
    List<ItemResponse> getItems();
    List<PaymentPurposeResponse> getPayments();
}
