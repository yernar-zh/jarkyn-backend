package kz.jarkyn.backend.model.group.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.good.api.ImmutableGroupEditApi;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableGroupEditApi.Builder.class)
public interface GroupEditApi {
    String getName();
    @Nullable IdApi getParent();
}
