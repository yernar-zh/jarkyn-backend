
package kz.jarkyn.backend.service;


import kz.jarkyn.backend.model.common.api.IdApi;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.good.GoodTransportEntity;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.repository.GoodRepository;
import kz.jarkyn.backend.repository.GoodTransportRepository;
import kz.jarkyn.backend.repository.TransportRepository;
import kz.jarkyn.backend.service.mapper.GoodMapper;
import kz.jarkyn.backend.service.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GoodService {
    private final GoodRepository goodRepository;
    private final TransportRepository transportRepository;
    private final GoodTransportRepository goodTransportRepository;
    private final GoodMapper goodMapper;

    public GoodService(
            GoodRepository goodRepository,
            TransportRepository transportRepository,
            GoodTransportRepository goodTransportRepository,
            GoodMapper goodMapper) {
        this.goodRepository = goodRepository;
        this.transportRepository = transportRepository;
        this.goodTransportRepository = goodTransportRepository;
        this.goodMapper = goodMapper;
    }

    @Transactional(readOnly = true)
    public GoodDetailApi findApiById(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow();
        List<GoodTransportEntity> goodTransports = goodTransportRepository.findByGood(good);
        return goodMapper.toDetailApi(good, goodTransports);
    }

    @Transactional(readOnly = true)
    public List<GoodListApi> findApiByFilter() {
        return null;
    }

    @Transactional
    public GoodDetailApi createApi(GoodCreateApi createApi) {
        GoodEntity good = goodRepository.save(goodMapper.toEntity(createApi));
        for (IdApi api : createApi.getTransports()) {
            GoodTransportEntity newEntity = new GoodTransportEntity();
            newEntity.setGood(good);
            newEntity.setTransport(transportRepository.findById(api.getId()).orElseThrow());
            goodTransportRepository.save(newEntity);
        }
        List<GoodTransportEntity> goodTransports = goodTransportRepository.findByGood(good);
        return goodMapper.toDetailApi(good, goodTransports);
    }


    @Transactional
    public GoodDetailApi editApi(UUID id, GoodEditApi editApi) {
        GoodEntity good = goodRepository.findById(id).orElseThrow();
        goodMapper.editEntity(good, editApi);
        EntityDivider<GoodTransportEntity, IdApi, UUID> divider = new EntityDivider<>(
                goodTransportRepository.findByGood(good), x -> x.getTransport().getId(),
                editApi.getTransports(), IdApi::getId
        );
        for (IdApi api : divider.newReceived()) {
            GoodTransportEntity newEntity = new GoodTransportEntity();
            newEntity.setGood(good);
            newEntity.setTransport(transportRepository.findById(api.getId()).orElseThrow());
            goodTransportRepository.save(newEntity);
        }
        goodTransportRepository.deleteAll(divider.skippedCurrent());
        List<GoodTransportEntity> goodTransports = goodTransportRepository.findByGood(good);
        return goodMapper.toDetailApi(good, goodTransports);
    }

}
