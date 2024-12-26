package kz.jarkyn.backend.counterparty.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseRequest;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface WarehouseMapper extends RequestResponseMapper<WarehouseEntity, WarehouseRequest, WarehouseResponse> {
}
