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
    public abstract List<AttributeGroupListApi> toListApi(List<AttributeGroupEntity> entity);
    public abstract AttributeGroupEntity toEntity(AttributeGroupCreateApi api);
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "attributes", source = "attributes")
    public abstract AttributeGroupDetailApi toDetailApi(AttributeGroupEntity entity, List<AttributeEntity> attributes);
    public abstract void editEntity(@MappingTarget AttributeGroupEntity entity, AttributeGroupEditApi api);

    public abstract AttributeEntity toEntity(AttributeApi api);
    @Mapping(target = "id", ignore = true)
    public abstract void editEntity(@MappingTarget AttributeEntity attributeEntity, AttributeApi received);
}
