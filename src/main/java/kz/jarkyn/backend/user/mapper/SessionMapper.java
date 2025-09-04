package kz.jarkyn.backend.user.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.user.model.RoleEntity;
import kz.jarkyn.backend.user.model.dto.AccessTokenResponse;
import kz.jarkyn.backend.user.model.dto.LoginResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface SessionMapper {
    AccessTokenResponse toAccessTokenResponse(String token, RoleEntity role);
    LoginResponse toLoginResponse(String refreshToken, AccessTokenResponse accessToken);
}
