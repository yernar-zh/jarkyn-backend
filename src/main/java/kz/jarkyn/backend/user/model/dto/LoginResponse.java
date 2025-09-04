package kz.jarkyn.backend.user.model.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface LoginResponse {
    String getRefreshToken();
    AccessTokenResponse getAccessToken();
}
