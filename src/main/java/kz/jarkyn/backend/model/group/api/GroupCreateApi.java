package kz.jarkyn.backend.model.group.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.NamedDto;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGroupCreateApi.Builder.class)
public interface GroupCreateApi extends NamedDto {
    @Nullable IdDto getParent();
}
