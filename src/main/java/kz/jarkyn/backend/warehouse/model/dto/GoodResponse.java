package kz.jarkyn.backend.warehouse.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.ReferenceResponse;
import kz.jarkyn.backend.operation.mode.dto.StockResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
public interface GoodResponse extends ReferenceResponse {
    ReferenceResponse getGroup();
    @Nullable IdDto getImage();
    BigDecimal getWeight();
    Integer getMinimumPrice();
    List<ReferenceResponse> getAttributes();
    List<SellingPriceResponse> getSellingPrices();
    List<StockResponse> getStock();
}
