
package kz.jarkyn.backend.service.mapper;

import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.good.GroupEntity;
import kz.jarkyn.backend.model.good.TransportEntity;
import kz.jarkyn.backend.repository.GoodRepository;
import kz.jarkyn.backend.repository.GroupRepository;
import kz.jarkyn.backend.repository.TransportRepository;
import org.springframework.stereotype.Service;

@Service
public class EntityMapper {
    private final GoodRepository goodRepository;
    private final GroupRepository groupRepository;
    private final TransportRepository transportRepository;

    public EntityMapper(
            GoodRepository goodRepository,
            GroupRepository groupRepository,
            TransportRepository transportRepository
    ) {
        this.goodRepository = goodRepository;
        this.groupRepository = groupRepository;
        this.transportRepository = transportRepository;
    }

    public GoodEntity toGoodEntity(IdApi api) {
        if (api == null) {
            return null;
        }
        return goodRepository.findById(api.getId()).orElseThrow();
    }

    public GroupEntity toGroupEntity(IdApi api) {
        if (api == null) {
            return null;
        }
        return groupRepository.findById(api.getId()).orElseThrow();
    }

    public TransportEntity toTransportEntity(IdApi api) {
        if (api == null) {
            return null;
        }
        return transportRepository.findById(api.getId()).orElseThrow();
    }
}
