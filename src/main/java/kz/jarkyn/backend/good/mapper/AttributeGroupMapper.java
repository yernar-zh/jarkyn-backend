package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import kz.jarkyn.backend.good.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.good.model.dto.AttributeGroupResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface AttributeGroupMapper extends RequestResponseMapper<AttributeGroupEntity, AttributeGroupRequest, AttributeGroupResponse> {
}
