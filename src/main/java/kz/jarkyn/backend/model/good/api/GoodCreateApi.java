package kz.jarkyn.backend.model.good.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import kz.jarkyn.backend.model.good.dto.SellingPriceDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGoodCreateApi.Builder.class)
public interface GoodCreateApi extends NamedDto {
    IdDto getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    List<IdDto> getAttributes();
    List<SellingPriceDto> getSellingPrices();
}
