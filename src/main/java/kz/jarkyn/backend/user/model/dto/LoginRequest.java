package kz.jarkyn.backend.user.model.dto;

import tools.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(builder = ImmutableLoginRequest.Builder.class)
public interface LoginRequest {
    String getPhoneNumber();
    String getPassword();
}
