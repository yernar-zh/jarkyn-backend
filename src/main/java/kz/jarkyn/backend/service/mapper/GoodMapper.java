
package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.common.api.IdNamedApi;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.good.api.GoodCreateApi;
import kz.jarkyn.backend.model.good.api.GoodDetailApi;
import kz.jarkyn.backend.model.good.api.GoodEditApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = EntityMapper.class)
public abstract class GoodMapper {
    public abstract GoodEntity toEntity(GoodCreateApi api);
    public abstract void editEntity(@MappingTarget GoodEntity entity, GoodEditApi api);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "group", source = "entity.group")
    @Mapping(target = "image", source = "entity.image")
    @Mapping(target = "minimumPrice", source = "entity.minimumPrice")
    @Mapping(target = "attributes", source = "goodAttributes")
    public abstract GoodDetailApi toDetailApi(GoodEntity entity, List<GoodAttributeEntity> goodAttributes);

    protected abstract IdNamedApi toApi(AttributeEntity entity);

    protected List<IdNamedApi> mapGoodTransports(List<GoodAttributeEntity> goodTransports) {
        return goodTransports.stream()
                .map(GoodAttributeEntity::getAttribute)
                .map(this::toApi)
                .collect(Collectors.toList());
    }
}
