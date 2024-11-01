
package kz.jarkyn.backend.sale.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import kz.jarkyn.backend.document.model.dto.ItemRequest;
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
