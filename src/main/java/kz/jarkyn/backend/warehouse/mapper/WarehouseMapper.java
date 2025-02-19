package kz.jarkyn.backend.warehouse.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseRequest;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface WarehouseMapper extends RequestResponseMapper<WarehouseEntity, WarehouseRequest, WarehouseResponse> {
}
