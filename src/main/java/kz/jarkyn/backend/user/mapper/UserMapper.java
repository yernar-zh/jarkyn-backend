package kz.jarkyn.backend.user.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.user.model.UserEntity;
import kz.jarkyn.backend.user.model.dto.UserRequest;
import kz.jarkyn.backend.user.model.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface UserMapper extends RequestResponseMapper<UserEntity, UserRequest, UserResponse> {
}
