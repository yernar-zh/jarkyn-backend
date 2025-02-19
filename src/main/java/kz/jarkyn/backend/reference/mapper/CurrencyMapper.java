package kz.jarkyn.backend.reference.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.core.mapper.ResponseMapper;
import kz.jarkyn.backend.reference.model.CurrencyEntity;
import kz.jarkyn.backend.reference.model.dto.CurrencyResponse;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseRequest;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = EntityMapper.class)
public interface CurrencyMapper extends ResponseMapper<CurrencyEntity, CurrencyResponse> {
}
