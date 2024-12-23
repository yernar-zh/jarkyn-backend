
package kz.jarkyn.backend.document.sale.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.NamedDto;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSaleRequest.Builder.class)
public interface SaleRequest extends NamedDto {
    @Nullable String getName();
    IdDto getWarehouse();
    IdDto getCounterparty();
    LocalDateTime getMoment();
    BigDecimal getAmount();
    String getComment();
    @Nullable LocalDateTime getShipmentMoment();
    List<ItemRequest> getItems();
}
