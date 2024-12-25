
package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.good.model.dto.GoodListResponse;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.good.model.dto.SellingPriceRequest;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.good.model.GoodAttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import kz.jarkyn.backend.good.model.dto.GoodRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(uses = EntityMapper.class)
public abstract class GoodMapper {
    public abstract GoodResponse toDetailApi(
            GoodEntity entity, List<AttributeEntity> attributes, List<SellingPriceEntity> sellingPrices);

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
    public abstract void editEntity(@MappingTarget SellingPriceEntity entity, SellingPriceRequest request);

    public abstract GoodListResponse toListResponse(
            GoodEntity entity, String attributes, BigDecimal sellingPrice, Integer remain);
}
