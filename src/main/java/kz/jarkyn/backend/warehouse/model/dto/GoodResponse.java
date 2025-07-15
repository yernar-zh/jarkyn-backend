package kz.jarkyn.backend.warehouse.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.global.model.dto.ImageResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
public interface GoodResponse extends ReferenceResponse {
    ReferenceResponse getGroup();
    @Nullable ImageResponse getImage();
    BigDecimal getWeight();
    Integer getMinimumPrice();
    List<ReferenceResponse> getAttributes();
    List<SellingPriceResponse> getSellingPrices();
    List<StockResponse> getStock();
}
