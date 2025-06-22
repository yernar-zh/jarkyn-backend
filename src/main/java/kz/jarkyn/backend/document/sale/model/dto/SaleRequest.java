
package kz.jarkyn.backend.document.sale.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.sale.model.SaleState;
import org.immutables.value.Value;

import java.time.Instant;
import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSaleRequest.Builder.class)
public interface SaleRequest extends DocumentRequest {
    IdDto getWarehouse();
    IdDto getCounterparty();
    @Nullable Instant getShipmentMoment();
    SaleState getState();
    List<ItemRequest> getItems();
}
