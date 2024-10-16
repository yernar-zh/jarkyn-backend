package kz.jarkyn.backend.model.common.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableValueApi.Builder.class)
public interface ValueApi<T> {
    T getValue();
}
