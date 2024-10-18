package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.attribute.AttributeGroupEntity;
import kz.jarkyn.backend.model.attribute.api.*;
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
    public abstract AttributeGroupEntity toEntity(AttributeGroupRequest api);
    public abstract void editEntity(@MappingTarget AttributeGroupEntity entity, AttributeGroupRequest api);

    public abstract AttributeResponse toDetailApi(AttributeEntity entity);
    public abstract AttributeEntity toEntity(AttributeRequest api);
    public abstract void editEntity(@MappingTarget AttributeEntity entity, AttributeEditRequest api);
}
