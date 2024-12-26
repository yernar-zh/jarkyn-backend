
package kz.jarkyn.backend.stock.mode.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(builder = ImmutableTurnoverRequest.Builder.class)
public interface TurnoverRequest {
    IdDto getDocument();
    IdDto getGood();
    Integer getQuantity();
    @Nullable BigDecimal getCostPrice();
}
