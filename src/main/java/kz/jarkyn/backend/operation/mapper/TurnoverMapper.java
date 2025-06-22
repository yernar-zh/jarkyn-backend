package kz.jarkyn.backend.operation.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.dto.StockResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(config = BaseMapperConfig.class)
public interface TurnoverMapper {
    StockResponse toStockResponse(WarehouseEntity warehouse, GoodEntity good, Integer remain, BigDecimal costPrice);
}
