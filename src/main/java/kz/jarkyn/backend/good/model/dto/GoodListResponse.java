
package kz.jarkyn.backend.good.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface GoodListResponse extends IdNamedDto {
    IdNamedDto getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    Boolean getArchived();
    String getAttributes();
    BigDecimal getSellingPrice();
    Integer getRemain();
}
