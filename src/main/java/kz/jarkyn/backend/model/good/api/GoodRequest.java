package kz.jarkyn.backend.model.good.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGoodRequest.Builder.class)
public interface GoodRequest extends NamedDto {
    IdDto getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    List<IdDto> getAttributes();
    List<SellingPriceRequest> getSellingPrices();
}
