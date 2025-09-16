package kz.jarkyn.backend.warehouse.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ReferenceRequest;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGoodRequest.Builder.class)
public interface GoodRequest extends ReferenceRequest {
    IdDto getGroup();
    @Nullable IdDto getImage();
    BigDecimal getWeight();
    Integer getMinimumPrice();
    String getSearchKeywords();
    List<IdDto> getAttributes();
    List<SellingPriceRequest> getSellingPrices();
}
