package kz.jarkyn.backend.good.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.stock.mode.dto.StockResponse;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GoodResponse extends IdNamedDto {
    IdNamedDto getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    Boolean getArchived();
    List<IdNamedDto> getAttributes();
    List<SellingPriceResponse> getSellingPrices();
    List<StockResponse> getStock();
}
