package kz.jarkyn.backend.model.group.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGroupRequest.Builder.class)
public interface GroupRequest extends NamedDto {
    @Nullable IdDto getParent();
    List<IdDto> getChildren();
}
