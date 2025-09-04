package kz.jarkyn.backend.user.model.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface AccessTokenResponse {
    String getToken();
    RoleResponse getRole();
}
