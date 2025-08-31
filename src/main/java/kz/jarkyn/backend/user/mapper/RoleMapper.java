package kz.jarkyn.backend.user.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.ResponseMapper;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.user.model.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface RoleMapper extends ResponseMapper<RoleEntity, EnumTypeResponse> {
}
