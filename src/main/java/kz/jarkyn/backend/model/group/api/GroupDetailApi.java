
package kz.jarkyn.backend.model.group.api;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface GroupDetailApi extends IdNamedDto {
    @Nullable IdNamedDto getParent();
    List<IdNamedDto> getChildren();
}
