
package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.repository.AttributeRepository;
import kz.jarkyn.backend.repository.GoodRepository;
import kz.jarkyn.backend.repository.GoodAttributeRepository;
import kz.jarkyn.backend.service.mapper.GoodMapper;
import kz.jarkyn.backend.service.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GoodService {
    private final GoodRepository goodRepository;
    private final GoodAttributeRepository goodAttributeRepository;
    private final AttributeRepository attributeRepository;
    private final GoodMapper goodMapper;

    public GoodService(
            GoodRepository goodRepository,
            GoodAttributeRepository goodAttributeRepository,
            AttributeRepository attributeRepository,
            GoodMapper goodMapper) {
        this.goodRepository = goodRepository;
        this.goodAttributeRepository = goodAttributeRepository;
        this.attributeRepository = attributeRepository;
        this.goodMapper = goodMapper;
    }

    @Transactional(readOnly = true)
    public GoodDetailApi findApiById(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<GoodAttributeEntity> goodTransports = goodAttributeRepository.findByGood(good);
        return goodMapper.toDetailApi(good, goodTransports);
    }

    @Transactional(readOnly = true)
    public List<GoodListApi> findApiByFilter() {
        return null;
    }

    @Transactional
    public GoodDetailApi createApi(GoodCreateApi createApi) {
        GoodEntity good = goodRepository.save(goodMapper.toEntity(createApi));
        good.setArchived(false);
        for (IdDto api : createApi.getAttributes()) {
            GoodAttributeEntity goodAttributeEntity = goodMapper.toEntity(good, api);
            goodAttributeRepository.save(goodAttributeEntity);
        }
        List<GoodAttributeEntity> goodTransports = goodAttributeRepository.findByGood(good);
        return goodMapper.toDetailApi(good, goodTransports);
    }


    @Transactional
    public GoodDetailApi editApi(UUID id, GoodEditApi editApi) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        goodMapper.editEntity(good, editApi);
        EntityDivider<GoodAttributeEntity, IdDto> divider = new EntityDivider<>(
                goodAttributeRepository.findByGood(good), editApi.getAttributes()
        );
        for (EntityDivider<GoodAttributeEntity, IdDto>.Entry entry : divider.newReceived()) {
            GoodAttributeEntity newEntity = new GoodAttributeEntity();
            newEntity.setGood(good);
            newEntity.setAttribute(attributeRepository.findById(entry.getReceived().getId()).orElseThrow());
            goodAttributeRepository.save(newEntity);
        }
        goodAttributeRepository.deleteAll(divider.skippedCurrent());
        goodRepository.save(good);
        return findApiById(id);
    }

    @Transactional
    public GoodDetailApi archive(UUID id, Boolean value) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        good.setArchived(value);
        goodRepository.save(good);
        return findApiById(id);
    }
}
