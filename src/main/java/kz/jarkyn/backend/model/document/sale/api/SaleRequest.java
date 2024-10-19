
package kz.jarkyn.backend.model.document.sale.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import kz.jarkyn.backend.model.document.common.api.ItemRequest;
import org.immutables.value.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSaleRequest.Builder.class)
public interface SaleRequest extends NamedDto {
    IdDto getWarehouse();
    IdDto getCustomer();
    LocalDateTime getMoment();
    Integer getAmount();
    String getComment();
    @Nullable LocalDateTime getShipmentMoment();
    List<ItemRequest> getItems();
}
