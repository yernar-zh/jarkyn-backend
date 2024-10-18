
package kz.jarkyn.backend.model.good.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableSellingPriceRequest.Builder.class)
public interface SellingPriceRequest extends IdDto {
    @Nullable UUID getId();
    Integer getQuantity();
    Integer getValue();
}
