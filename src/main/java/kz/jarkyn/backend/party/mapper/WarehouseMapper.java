package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.WarehouseEntity;
import kz.jarkyn.backend.party.model.dto.WarehouseRequest;
import kz.jarkyn.backend.party.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface WarehouseMapper extends RequestResponseMapper<WarehouseEntity, WarehouseRequest, WarehouseResponse> {
}
