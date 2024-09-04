package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.good.GroupEntity;
import kz.jarkyn.backend.model.good.api.GroupCreateApi;
import kz.jarkyn.backend.model.good.api.GroupDetailApi;
import kz.jarkyn.backend.model.good.api.GroupListApi;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public abstract class GroupMapper {
    public GroupListApi toListApi(GroupEntity entity, Map<GroupEntity, List<GroupEntity>> childrenMap) {
        List<GroupListApi> children = new ArrayList<>();
        for (GroupEntity child : childrenMap.get(entity)) {
            children.add(toListApi(child, childrenMap));
        }
        return toListApi(entity, children);
    }

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "children", source = "children")
    protected abstract GroupListApi toListApi(GroupEntity entity, List<GroupListApi> children);
    public abstract GroupDetailApi toDetailApi(GroupEntity entity);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    public abstract GroupEntity toEntity(GroupCreateApi api);
}
