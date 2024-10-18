
package kz.jarkyn.backend.service;


import kz.jarkyn.backend.exception.ExceptionUtils;
import kz.jarkyn.backend.model.attribute.AttributeEntity;
import kz.jarkyn.backend.model.common.dto.IdDto;
import kz.jarkyn.backend.utils.PrefixSearch;
import kz.jarkyn.backend.model.good.GoodAttributeEntity;
import kz.jarkyn.backend.model.good.GoodEntity;
import kz.jarkyn.backend.model.good.SellingPriceEntity;
import kz.jarkyn.backend.model.good.api.*;
import kz.jarkyn.backend.model.good.apiFilter.GoodApiFilter;
import kz.jarkyn.backend.model.good.dto.GoodDto;
import kz.jarkyn.backend.model.good.api.SellingPriceRequest;
import kz.jarkyn.backend.repository.AttributeRepository;
import kz.jarkyn.backend.repository.GoodRepository;
import kz.jarkyn.backend.repository.GoodAttributeRepository;
import kz.jarkyn.backend.repository.SellingPriceRepository;
import kz.jarkyn.backend.service.mapper.GoodMapper;
import kz.jarkyn.backend.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Service
public class GoodService {
    private final GoodRepository goodRepository;
    private final GoodAttributeRepository goodAttributeRepository;
    private final AttributeRepository attributeRepository;
    private final SellingPriceRepository sellingPriceRepository;
    private final GoodMapper goodMapper;

    public GoodService(
            GoodRepository goodRepository,
            GoodAttributeRepository goodAttributeRepository,
            AttributeRepository attributeRepository,
            SellingPriceRepository sellingPriceRepository,
            GoodMapper goodMapper) {
        this.goodRepository = goodRepository;
        this.goodAttributeRepository = goodAttributeRepository;
        this.attributeRepository = attributeRepository;
        this.sellingPriceRepository = sellingPriceRepository;
        this.goodMapper = goodMapper;
    }

    @Transactional(readOnly = true)
    public GoodResponse findApiById(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<AttributeEntity> attributes = attributeRepository.findByGood(good);
        attributes.sort(Comparator.comparing(AttributeEntity::getName));
        List<SellingPriceEntity> sellingPrices = sellingPriceRepository.findByGood(good);
        sellingPrices.sort(Comparator.comparing(SellingPriceEntity::getQuantity));
        return goodMapper.toDetailApi(good, attributes, sellingPrices);
    }

    @Transactional(readOnly = true)
    public List<GoodResponse> findApiByFilter(GoodApiFilter filter) {
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
        return stream.map(goodMapper::toListApi).toList();
    }

    @Transactional
    public GoodResponse createApi(GoodRequest createApi) {
        GoodEntity good = goodRepository.save(goodMapper.toEntity(createApi));
        good.setArchived(false);
        for (IdDto api : createApi.getAttributes()) {
            GoodAttributeEntity goodAttributeEntity = goodMapper.toEntity(good, api);
            goodAttributeRepository.save(goodAttributeEntity);
        }
        for (SellingPriceRequest sellingPrice : createApi.getSellingPrices()) {
            SellingPriceEntity sellingPriceEntity = goodMapper.toEntity(good, sellingPrice);
            sellingPriceRepository.save(sellingPriceEntity);
        }
        return findApiById(good.getId());
    }

    @Transactional
    public GoodResponse editApi(UUID id, GoodRequest editApi) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        goodMapper.editEntity(good, editApi);

        EntityDivider<AttributeEntity, IdDto> attributeDivider = new EntityDivider<>(
                attributeRepository.findByGood(good), editApi.getAttributes()
        );
        for (EntityDivider<AttributeEntity, IdDto>.Entry entry : attributeDivider.newReceived()) {
            GoodAttributeEntity goodAttributeEntity = goodMapper.toEntity(good, entry.getReceived());
            goodAttributeRepository.save(goodAttributeEntity);
        }
        for (AttributeEntity attribute : attributeDivider.skippedCurrent()) {
            GoodAttributeEntity goodAttribute = goodAttributeRepository
                    .findByGoodAndAttribute(good, attribute).orElseThrow();
            goodAttributeRepository.delete(goodAttribute);
        }

        EntityDivider<SellingPriceEntity, SellingPriceRequest> sellingPriceDivider = new EntityDivider<>(
                sellingPriceRepository.findByGood(good), editApi.getSellingPrices()
        );
        for (EntityDivider<SellingPriceEntity, SellingPriceRequest>.Entry entry : sellingPriceDivider.newReceived()) {
            SellingPriceEntity sellingPrice = goodMapper.toEntity(good, entry.getReceived());
            sellingPriceRepository.save(sellingPrice);
        }
        for (EntityDivider<SellingPriceEntity, SellingPriceRequest>.Entry entry : sellingPriceDivider.edited()) {
            goodMapper.editEntity(entry.getCurrent(), entry.getReceived());
        }
        sellingPriceRepository.deleteAll(sellingPriceDivider.skippedCurrent());
        return findApiById(id);
    }

    @Transactional
    public GoodResponse archive(UUID id, Boolean value) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        good.setArchived(value);
        goodRepository.save(good);
        return findApiById(id);
    }

    private List<GoodDto> findAllDto() {
        List<GoodDto> result = new ArrayList<>();
        List<GoodEntity> goods = goodRepository.findAll();
        for (GoodEntity good : goods) {
            List<AttributeEntity> attributes = attributeRepository.findByGood(good);
            List<SellingPriceEntity> sellingPrices = sellingPriceRepository.findByGood(good);
            PrefixSearch prefixSearch = new PrefixSearch();
            prefixSearch.add(good.getName());
            prefixSearch.add(good.getGroup().getName());
            result.add(goodMapper.toDto(good, attributes, sellingPrices, prefixSearch));
        }
        return result;
    }

}
