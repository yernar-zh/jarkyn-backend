package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import kz.jarkyn.backend.good.model.dto.AttributeGroupDetailResponse;
import kz.jarkyn.backend.good.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.good.model.dto.AttributeGroupResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface AttributeGroupMapper extends RequestResponseMapper<AttributeGroupEntity, AttributeGroupRequest, AttributeGroupResponse> {
    AttributeGroupDetailResponse toResponse(AttributeGroupEntity entity, List<AttributeEntity> attributes);
}
