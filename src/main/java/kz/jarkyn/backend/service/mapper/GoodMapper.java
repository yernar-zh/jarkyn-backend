
package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.exception.DataValidationException;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import kz.jarkyn.backend.model.common.dto.PrefixSearch;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.good.api.GoodCreateApi;
import kz.jarkyn.backend.model.good.api.GoodDetailApi;
import kz.jarkyn.backend.model.good.api.GoodEditApi;
import kz.jarkyn.backend.model.good.dto.GoodDto;
import kz.jarkyn.backend.model.group.GroupEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(uses = EntityMapper.class)
public abstract class GoodMapper {
    public abstract GoodEntity toEntity(GoodCreateApi api);
    public abstract void editEntity(@MappingTarget GoodEntity entity, GoodEditApi api);
    @Mapping(target = "attributes", source = "goodAttributes")
    public abstract GoodDetailApi toDetailApi(GoodEntity entity, List<GoodAttributeEntity> goodAttributes);
    public abstract GoodDto toDto(GoodEntity entity, List<GoodAttributeEntity> attributes, PrefixSearch prefixSearch);
    protected abstract IdNamedDto toApi(AttributeEntity entity);
    protected abstract IdNamedDto toApi(GroupEntity entity);

    protected List<IdNamedDto> mapGoodAttributes(List<GoodAttributeEntity> goodTransports) {
        return goodTransports.stream()
                .map(GoodAttributeEntity::getAttribute)
                .map(this::toApi)
                .collect(Collectors.toList());
    }

    protected List<IdNamedDto> toGroupList(GroupEntity entity) {
        return Stream.iterate(entity, Objects::nonNull, GroupEntity::getParent)
                .map(this::toApi).collect(Collectors.toCollection(ArrayList::new));
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "attribute", source = "attribute")
    public abstract GoodAttributeEntity toEntity(GoodEntity good, IdDto attribute);
}
