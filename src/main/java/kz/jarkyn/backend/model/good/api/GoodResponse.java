
package kz.jarkyn.backend.model.good.api;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import kz.jarkyn.backend.model.good.dto.SellingPriceDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GoodResponse extends IdNamedDto {
    IdNamedDto getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    Boolean getArchived();
    List<IdNamedDto> getAttributes();
    List<SellingPriceDto> getSellingPrices();
}
