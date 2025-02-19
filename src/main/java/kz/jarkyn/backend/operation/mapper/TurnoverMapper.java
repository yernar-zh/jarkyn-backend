package kz.jarkyn.backend.operation.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.good.model.WarehouseEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.operation.mode.dto.StockResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(uses = EntityMapper.class)
public interface TurnoverMapper {
    StockResponse toStockResponse(WarehouseEntity warehouse, GoodEntity good, Integer remain, BigDecimal costPrice);
}
