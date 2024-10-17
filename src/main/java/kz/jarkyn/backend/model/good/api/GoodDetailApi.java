
package kz.jarkyn.backend.model.good.api;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GoodDetailApi extends IdNamedDto {
    IdNamedDto getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    Boolean getArchived();
    List<IdNamedDto> getAttributes();
}
