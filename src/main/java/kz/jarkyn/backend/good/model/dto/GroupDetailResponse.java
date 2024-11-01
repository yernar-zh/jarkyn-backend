
package kz.jarkyn.backend.good.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupDetailResponse extends IdNamedDto {
    @Nullable IdNamedDto getParent();
    List<IdNamedDto> getChildren();
}
