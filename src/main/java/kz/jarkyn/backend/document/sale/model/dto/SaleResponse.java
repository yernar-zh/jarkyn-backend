
package kz.jarkyn.backend.document.sale.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.payment.model.dto.PaidDocumentResponse;
import kz.jarkyn.backend.document.sale.model.SaleState;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value.Immutable
public interface SaleResponse extends DocumentResponse {
    ReferenceResponse getWarehouse();
    ReferenceResponse getCounterparty();
    @Nullable LocalDateTime getShipmentMoment();
    SaleState getState();
    List<ItemResponse> getItems();
    List<PaidDocumentResponse> getPaidDocuments();
}
