package kz.jarkyn.backend.warehouse.mapper;

import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.warehouse.model.GroupEntity;
import kz.jarkyn.backend.warehouse.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.warehouse.model.dto.GroupRequest;
import kz.jarkyn.backend.warehouse.model.dto.GroupResponse;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import org.mapstruct.Mapper;

import java.util.*;

@Mapper(uses = EntityMapper.class)
public abstract class GroupMapper implements RequestMapper<GroupEntity, GroupRequest> {
    public GroupResponse toListApi(GroupEntity entity, Map<GroupEntity, List<GroupEntity>> childrenMap) {
        List<GroupResponse> children = childrenMap.getOrDefault(entity, List.of()).stream()
                .map(child -> toListApi(child, childrenMap))
                .toList();
        return toListApi(entity, children);
    }

    protected abstract GroupResponse toListApi(GroupEntity entity, List<GroupResponse> children);
    public abstract GroupDetailResponse toDetailApi(GroupEntity entity, List<GroupEntity> children);
}
