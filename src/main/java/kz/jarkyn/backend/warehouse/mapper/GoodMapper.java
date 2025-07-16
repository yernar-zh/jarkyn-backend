
package kz.jarkyn.backend.warehouse.mapper;

import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.operation.service.TurnoverService;
import kz.jarkyn.backend.warehouse.model.*;
import kz.jarkyn.backend.warehouse.model.dto.GoodListResponse;
import kz.jarkyn.backend.warehouse.model.dto.GoodResponse;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.warehouse.model.dto.GoodRequest;
import kz.jarkyn.backend.warehouse.model.dto.StockResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public abstract class GoodMapper implements RequestMapper<GoodEntity, GoodRequest> {
    public abstract GoodResponse toResponse(
            GoodEntity entity, List<AttributeEntity> attributes, List<SellingPriceEntity> sellingPrices,
            List<TurnoverService.StockDto> stocks);
    public abstract GoodListResponse toListResponse(
            GoodEntity entity, String path, String groupIds, String attributeIds,
            BigDecimal sellingPrice, Integer remain, BigDecimal costPrice);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "good", source = "good")
    @Mapping(target = "attribute", source = "attribute")
    public abstract GoodAttributeEntity toEntity(GoodEntity good, IdDto attribute);
}
