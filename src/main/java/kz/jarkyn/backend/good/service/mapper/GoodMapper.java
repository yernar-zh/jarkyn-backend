
package kz.jarkyn.backend.good.service.mapper;

import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.good.model.dto.SellingPriceRequest;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.core.service.mapper.EntityMapper;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import kz.jarkyn.backend.good.model.GoodAttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import kz.jarkyn.backend.good.model.dto.GoodRequest;
import kz.jarkyn.backend.good.model.dto.GoodDto;
import kz.jarkyn.backend.good.model.GroupEntity;
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
    public abstract GoodResponse toDetailApi(
            GoodEntity entity, List<AttributeEntity> attributes, List<SellingPriceEntity> sellingPrices);
    public abstract GoodResponse toListApi(GoodDto entity);

    public abstract GoodEntity toEntity(GoodRequest request);
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "attribute", source = "attribute")
    public abstract GoodAttributeEntity toEntity(GoodEntity good, IdDto attribute);
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "quantity", source = "sellingPrice.quantity")
    @Mapping(target = "value", source = "sellingPrice.value")
    public abstract SellingPriceEntity toEntity(GoodEntity good, SellingPriceRequest sellingPrice);

    public abstract void editEntity(@MappingTarget GoodEntity entity, GoodRequest request);
    public abstract void editEntity(@MappingTarget SellingPriceEntity entity, SellingPriceRequest dto);

    public abstract GoodDto toDto(
            GoodEntity entity, List<AttributeEntity> attributes, List<SellingPriceEntity> sellingPrices,
            PrefixSearch search);

    protected IdNamedDto map(List<IdNamedDto> value) {
        if (value.isEmpty()) return null;
        return value.getFirst();
    }
    protected List<IdNamedDto> toGroupList(GroupEntity entity) {
        return Stream.iterate(entity, Objects::nonNull, GroupEntity::getParent)
                .map(this::toApi).collect(Collectors.toCollection(ArrayList::new));
    }
    protected abstract IdNamedDto toApi(GroupEntity entity);


}