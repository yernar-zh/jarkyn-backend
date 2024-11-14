package kz.jarkyn.backend.counterparty.service.mapper;

import kz.jarkyn.backend.core.service.mapper.EntityMapper;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseRequest;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = EntityMapper.class)
public abstract class WarehouseMapper {
    public abstract WarehouseResponse toApi(WarehouseEntity entity);
    public abstract WarehouseEntity toEntity(WarehouseRequest request);
    public abstract void editEntity(@MappingTarget WarehouseEntity entity, WarehouseRequest request);
}
