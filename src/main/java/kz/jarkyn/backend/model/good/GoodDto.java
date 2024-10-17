
package kz.jarkyn.backend.model.good;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import org.immutables.value.Value;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Value.Immutable
public interface GoodDto extends IdNamedDto {
    IdNamedDto getGroup();
    @Nullable IdDto getImage();
    Integer getMinimumPrice();
    List<IdNamedDto> getAttributes();
    SortedMap<String> pattern
}
