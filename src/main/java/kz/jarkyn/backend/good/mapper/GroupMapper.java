package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.good.model.GroupEntity;
import kz.jarkyn.backend.good.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.good.model.dto.GroupRequest;
import kz.jarkyn.backend.good.model.dto.GroupResponse;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(uses = EntityMapper.class)
public abstract class GroupMapper {
    public GroupResponse toListApi(GroupEntity entity, Map<GroupEntity, List<GroupEntity>> childrenMap) {
        List<GroupResponse> children = new ArrayList<>();
        for (GroupEntity child : childrenMap.getOrDefault(entity, List.of())) {
            children.add(toListApi(child, childrenMap));
        }
        return toListApi(entity, children);
    }

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    protected abstract GroupResponse toListApi(GroupEntity entity, List<GroupResponse> children);
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "parent", source = "entity.parent")
    public abstract GroupDetailResponse toDetailApi(GroupEntity entity, List<GroupEntity> children);
    public abstract GroupEntity toEntity(GroupRequest request);
    public abstract void editEntity(@MappingTarget GroupEntity entity, GroupRequest request);
}
