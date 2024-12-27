package kz.jarkyn.backend.stock.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
import kz.jarkyn.backend.stock.mode.dto.StockResponse;
import kz.jarkyn.backend.stock.mode.dto.TurnoverRequest;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(uses = EntityMapper.class)
public interface TurnoverMapper {
    StockResponse toStockResponse(WarehouseEntity warehouse, GoodEntity good, Integer remain, BigDecimal costPrice);
}
