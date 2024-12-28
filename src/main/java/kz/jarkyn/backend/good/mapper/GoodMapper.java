
package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.good.model.dto.GoodListResponse;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.good.model.GoodAttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import kz.jarkyn.backend.good.model.dto.GoodRequest;
import kz.jarkyn.backend.operation.mode.dto.StockResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface GoodMapper extends RequestMapper<GoodEntity, GoodRequest> {
    GoodResponse toResponse(
            GoodEntity entity, List<AttributeEntity> attributes, List<SellingPriceEntity> sellingPrices,
            List<StockResponse> stock);
    GoodListResponse toListResponse(
            GoodEntity entity, String attributes, BigDecimal sellingPrice, Integer remain, BigDecimal costPrice);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "attribute", source = "attribute")
    GoodAttributeEntity toEntity(GoodEntity good, IdDto attribute);
}
