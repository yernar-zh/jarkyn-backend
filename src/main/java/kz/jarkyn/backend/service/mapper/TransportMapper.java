package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.good.TransportEntity;
import kz.jarkyn.backend.model.good.api.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(uses = EntityMapper.class)
public abstract class TransportMapper {
    public TransportListApi toListApi(TransportEntity entity, Map<TransportEntity, List<TransportEntity>> childrenMap) {
        List<TransportListApi> children = new ArrayList<>();
        for (TransportEntity child : childrenMap.getOrDefault(entity, List.of())) {
            children.add(toListApi(child, childrenMap));
        }
        return toListApi(entity, children);
    }

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    protected abstract TransportListApi toListApi(TransportEntity entity, List<TransportListApi> children);
    @Mapping(target = "parent.parent", ignore = true)
    public abstract TransportDetailApi toDetailApi(TransportEntity entity);
    public abstract TransportEntity toEntity(TransportCreateApi api);
    public abstract void editEntity(@MappingTarget TransportEntity entity, TransportEditApi api);
}
