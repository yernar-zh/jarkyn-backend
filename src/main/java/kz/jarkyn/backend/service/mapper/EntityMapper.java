
package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.good.GroupEntity;
import kz.jarkyn.backend.repository.GroupRepository;
import org.springframework.stereotype.Service;

@Service
public class EntityMapper {
    private final GroupRepository groupRepository;

    public EntityMapper(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public GroupEntity toGroupEntity(IdApi api) {
        if (api == null) {
            return null;
        }
        return groupRepository.findById(api.getId()).orElseThrow();
    }
}
