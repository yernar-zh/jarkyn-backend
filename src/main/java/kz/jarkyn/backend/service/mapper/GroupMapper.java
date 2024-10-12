package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.group.GroupEntity;
import kz.jarkyn.backend.model.group.api.GroupCreateApi;
import kz.jarkyn.backend.model.group.api.GroupDetailApi;
import kz.jarkyn.backend.model.group.api.GroupEditApi;
import kz.jarkyn.backend.model.group.api.GroupListApi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(uses = EntityMapper.class)
public abstract class GroupMapper {
    public GroupListApi toListApi(GroupEntity entity, Map<GroupEntity, List<GroupEntity>> childrenMap) {
        List<GroupListApi> children = new ArrayList<>();
        for (GroupEntity child : childrenMap.getOrDefault(entity, List.of())) {
            children.add(toListApi(child, childrenMap));
        }
        return toListApi(entity, children);
    }

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    protected abstract GroupListApi toListApi(GroupEntity entity, List<GroupListApi> children);
    @Mapping(target = "parent.parent", ignore = true)
    public abstract GroupDetailApi toDetailApi(GroupEntity entity);
    public abstract GroupEntity toEntity(GroupCreateApi api);
    public abstract void editEntity(@MappingTarget GroupEntity entity, GroupEditApi api);
}
