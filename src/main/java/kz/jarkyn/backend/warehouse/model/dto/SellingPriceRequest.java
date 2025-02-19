
package kz.jarkyn.backend.warehouse.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSellingPriceRequest.Builder.class)
public interface SellingPriceRequest extends IdDto {
    @Nullable UUID getId();
    Integer getQuantity();
    BigDecimal getValue();
}
