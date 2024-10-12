package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.attribute.AttributeGroupEntity;
import kz.jarkyn.backend.model.attribute.api.*;
import kz.jarkyn.backend.model.common.api.IdApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public abstract class AttributeMapper {
    public abstract List<AttributeGroupListApi> toListApi(List<AttributeGroupEntity> entity);
    public abstract AttributeGroupEntity toEntity(AttributeGroupCreateApi api);
    public abstract AttributeGroupDetailApi toDetailApi(AttributeGroupEntity entity);
    public abstract void editEntity(@MappingTarget AttributeGroupEntity entity, AttributeGroupEditApi api);

    public abstract AttributeEntity toEntity(AttributeApi api);
}
