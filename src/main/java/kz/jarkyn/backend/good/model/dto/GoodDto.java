
package kz.jarkyn.backend.good.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GoodDto extends IdNamedDto {
    List<IdNamedDto> getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    Boolean getArchived();
    List<IdNamedDto> getAttributes();
    List<SellingPriceDto> getSellingPrices();
    PrefixSearch getSearch();
}
