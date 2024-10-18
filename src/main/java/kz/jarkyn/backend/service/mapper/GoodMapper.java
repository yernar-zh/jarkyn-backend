
package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.exception.DataValidationException;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.IdNamedDto;
import kz.jarkyn.backend.model.common.dto.PrefixSearch;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.good.SellingPriceEntity;
import kz.jarkyn.backend.model.good.api.GoodCreateApi;
import kz.jarkyn.backend.model.good.api.GoodDetailApi;
import kz.jarkyn.backend.model.good.api.GoodEditApi;
import kz.jarkyn.backend.model.good.api.GoodListApi;
import kz.jarkyn.backend.model.good.dto.GoodDto;
import kz.jarkyn.backend.model.good.dto.SellingPriceDto;
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
    public abstract GoodDetailApi toDetailApi(
            GoodEntity entity, List<AttributeEntity> attributes, List<SellingPriceEntity> sellingPrices);
    public abstract GoodListApi toListApi(GoodDto entity);

    public abstract GoodEntity toEntity(GoodCreateApi api);
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "attribute", source = "attribute")
    public abstract GoodAttributeEntity toEntity(GoodEntity good, IdDto attribute);
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "quantity", source = "sellingPrice.quantity")
    @Mapping(target = "value", source = "attribute.value")
    public abstract SellingPriceEntity toEntity(GoodEntity good, SellingPriceDto sellingPrice);

    public abstract void editEntity(@MappingTarget GoodEntity entity, GoodEditApi api);
    public abstract void editEntity(@MappingTarget SellingPriceEntity entity, SellingPriceDto dto);

    public abstract GoodDto toDto(
            GoodEntity entity, List<AttributeEntity> attributes, List<SellingPriceEntity> sellingPrices,
            PrefixSearch prefixSearch);
    protected abstract IdNamedDto toApi(GroupEntity entity);

    protected List<IdNamedDto> mapGoodAttributes(List<GoodAttributeEntity> goodAttributes) {
        return goodTransports.stream()
                .map(GoodAttributeEntity::getAttribute)
                .map(this::toApi)
                .collect(Collectors.toList());
    }

    protected List<IdNamedDto> toGroupList(GroupEntity entity) {
        return Stream.iterate(entity, Objects::nonNull, GroupEntity::getParent)
                .map(this::toApi).collect(Collectors.toCollection(ArrayList::new));
    }


}
