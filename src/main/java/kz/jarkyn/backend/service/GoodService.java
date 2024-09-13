
package kz.jarkyn.backend.service;


import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.good.GoodTransportEntity;
import kz.jarkyn.backend.model.good.GroupEntity;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.repository.GoodRepository;
import kz.jarkyn.backend.repository.GoodTransportRepository;
import kz.jarkyn.backend.repository.GroupRepository;
import kz.jarkyn.backend.service.mapper.GoodMapper;
import kz.jarkyn.backend.service.mapper.GroupMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GoodService {
    private final GoodRepository goodRepository;
    private final GoodTransportRepository goodTransportEntity;
    private final GoodMapper goodMapper;

    public GoodService(
            GoodRepository goodRepository,
            GoodTransportRepository goodTransportEntity,
            GoodMapper goodMapper) {
        this.goodRepository = goodRepository;
        this.goodTransportEntity = goodTransportEntity;
        this.goodMapper = goodMapper;
    }

    @Transactional(readOnly = true)
    public List<GroupListApi> findApiBy() {
        return null;
    }

    @Transactional
    public GroupDetailApi createApi(GoodCreateApi createApi) {
        GoodEntity entity = goodMapper.toEntity(createApi);
        entity = goodRepository.save(entity);
        for (IdApi transportApi : createApi.getTransports()) {

            GoodTransportEntity goodTransport = goodTransportEntity.findById(transportApi.getId()).orElseThrow();
        }

        return goodMapper.toDetailApi(goodRepository.save(entity));
    }

    @Transactional
    public GroupDetailApi editApi(UUID id, GroupEditApi editApi) {
        GroupEntity entity = groupRepository.findById(id).orElseThrow();
        groupMapper.editEntity(entity, editApi);
        return groupMapper.toDetailApi(groupRepository.save(entity));
    }
}
