package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.warehouse.WarehouseEntity;
import kz.jarkyn.backend.model.warehouse.api.WarehouseRequest;
import kz.jarkyn.backend.model.warehouse.api.WarehouseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = EntityMapper.class)
public abstract class WarehouseMapper {
    public abstract WarehouseResponse toApi(WarehouseEntity entity);
    public abstract WarehouseEntity toEntity(WarehouseRequest request);
    public abstract void editEntity(@MappingTarget WarehouseEntity entity, WarehouseRequest request);
}
