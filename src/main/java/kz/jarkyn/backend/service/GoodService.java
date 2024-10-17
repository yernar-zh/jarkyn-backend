
package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.model.common.dto.PrefixSearch;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.model.good.apiFilter.GoodApiFilter;
import kz.jarkyn.backend.model.good.dto.GoodDto;
import kz.jarkyn.backend.repository.AttributeRepository;
import kz.jarkyn.backend.repository.GoodRepository;
import kz.jarkyn.backend.repository.GoodAttributeRepository;
import kz.jarkyn.backend.service.mapper.GoodMapper;
import kz.jarkyn.backend.service.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

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
        List<GoodAttributeEntity> goodAttributes = goodAttributeRepository.findByGood(good);
        return goodMapper.toDetailApi(good, goodAttributes);
    }

    @Transactional(readOnly = true)
    public List<GoodListApi> findApiByFilter(GoodApiFilter filter) {
        Stream<GoodDto> stream = findAllDto().stream();
        if (filter.getSearch() != null) {
            stream = stream.filter(goodDto -> goodDto.getPrefixSearch().contains(filter.getSearch()));
        }
        if (filter.getGroupId() != null) {
            stream = stream.filter(goodDto -> goodDto.getGroup().stream()
                    .map(IdDto::getId).anyMatch(filter.getGroupId()::equals));
        }
        if (filter.getAttributeId() != null) {
            stream = stream.filter(goodDto -> goodDto.getAttributes().stream()
                    .map(IdDto::getId).anyMatch(filter.getAttributeId()::equals));
        }
        return stream.map(goodDto -> goodMapper.toDetailApi(goodDto)).toList();
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

    private List<GoodDto> findAllDto() {
        List<GoodDto> result = new ArrayList<>();
        List<GoodEntity> goods = goodRepository.findAll();
        for (GoodEntity good : goods) {
            List<GoodAttributeEntity> goodTransports = goodAttributeRepository.findByGood(good);
            PrefixSearch prefixSearch = new PrefixSearch();
            prefixSearch.add(good.getName());
            prefixSearch.add(good.getGroup().getName());
            result.add(goodMapper.toDto(good, goodTransports, prefixSearch));
        }
        return result;
    }

}
