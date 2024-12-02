package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.good.model.dto.*;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.AttributeGroupEntity;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public abstract class AttributeMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "attributes", source = "attributes")
    public abstract AttributeGroupResponse toDetailApi(AttributeGroupEntity entity, List<AttributeEntity> attributes);
    public abstract List<AttributeGroupResponse> toListApi(List<AttributeGroupEntity> entity);
    public abstract AttributeGroupEntity toEntity(AttributeGroupRequest request);
    public abstract void editEntity(@MappingTarget AttributeGroupEntity entity, AttributeGroupRequest request);

    public abstract AttributeResponse toDetailApi(AttributeEntity entity);
    public abstract AttributeEntity toEntity(AttributeRequest request);
    public abstract void editEntity(@MappingTarget AttributeEntity entity, AttributeEditRequest request);
}
