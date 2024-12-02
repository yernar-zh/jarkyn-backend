package kz.jarkyn.backend.counterparty.mapper;

import kz.jarkyn.backend.core.service.mapper.EntityMapper;
import kz.jarkyn.backend.core.service.mapper.RequestResponseMapper;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseRequest;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = EntityMapper.class)
public abstract class WarehouseMapper extends
        RequestResponseMapper<WarehouseEntity, WarehouseRequest, WarehouseResponse> {
}
