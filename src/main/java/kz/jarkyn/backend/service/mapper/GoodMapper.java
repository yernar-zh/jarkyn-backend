
package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.good.api.GoodCreateApi;
import kz.jarkyn.backend.model.good.api.GoodDetailApi;
import kz.jarkyn.backend.model.good.api.GoodEditApi;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = EntityMapper.class)
public abstract class GoodMapper {
    public abstract GoodEntity toEntity(GoodCreateApi api);
    public abstract void editEntity(@MappingTarget GoodEntity entity, GoodEditApi api);

    @Mapping(target = "attributes", source = "goodAttributes")
    public abstract GoodDetailApi toDetailApi(GoodEntity entity, List<GoodAttributeEntity> goodAttributes);

    protected abstract IdNamedDto toApi(AttributeEntity entity);

    protected List<IdNamedDto> mapGoodTransports(List<GoodAttributeEntity> goodTransports) {
        return goodTransports.stream()
                .map(GoodAttributeEntity::getAttribute)
                .map(this::toApi)
                .collect(Collectors.toList());
    }

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "attribute", source = "attribute")
    public abstract GoodAttributeEntity toEntity(GoodEntity good, IdDto attribute);
}
