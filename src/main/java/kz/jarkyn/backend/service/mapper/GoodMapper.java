
package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.good.api.GoodCreateApi;
import kz.jarkyn.backend.model.good.api.GoodEditApi;
import kz.jarkyn.backend.model.good.api.GroupDetailApi;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = EntityMapper.class)
public abstract class GoodMapper {
    public abstract GoodEntity toEntity(GoodCreateApi api);
    public abstract void editEntity(@MappingTarget GoodEntity entity, GoodEditApi api);

    public GroupDetailApi toDetailApi(GoodEntity save) {
        return null;
    }
}
