
package kz.jarkyn.backend.document.core.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value.Immutable
@JsonDeserialize(builder = ImmutableItemRequest.Builder.class)
public interface ItemRequest extends IdDto {
    @Nullable UUID getId();
    IdDto getGood();
    Integer getPrice();
    BigDecimal getQuantity();
}
