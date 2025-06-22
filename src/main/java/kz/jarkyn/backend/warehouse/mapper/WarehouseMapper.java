package kz.jarkyn.backend.warehouse.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseRequest;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface WarehouseMapper extends RequestResponseMapper<WarehouseEntity, WarehouseRequest, WarehouseResponse> {
}
