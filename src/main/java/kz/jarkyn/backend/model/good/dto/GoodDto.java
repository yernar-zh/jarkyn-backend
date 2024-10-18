
package kz.jarkyn.backend.model.good.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import kz.jarkyn.backend.model.common.dto.PrefixSearch;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GoodDto extends IdNamedDto {
    List<IdNamedDto> getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    List<IdNamedDto> getAttributes();
    List<SellingPriceDto> getSellingPrices();
    PrefixSearch getPrefixSearch();
}
