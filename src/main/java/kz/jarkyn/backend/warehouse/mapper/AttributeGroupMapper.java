package kz.jarkyn.backend.warehouse.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.warehouse.model.AttributeEntity;
import kz.jarkyn.backend.warehouse.model.AttributeGroupEntity;
import kz.jarkyn.backend.warehouse.model.dto.AttributeGroupRequest;
import kz.jarkyn.backend.warehouse.model.dto.AttributeGroupResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface AttributeGroupMapper extends RequestResponseMapper<AttributeGroupEntity, AttributeGroupRequest, AttributeGroupResponse> {
    AttributeGroupResponse toResponse(AttributeGroupEntity entity, List<AttributeEntity> attributes);
}
