
package kz.jarkyn.backend.good.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.good.model.dto.GoodRequest;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import kz.jarkyn.backend.good.model.GoodAttributeEntity;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.good.model.SellingPriceEntity;
import kz.jarkyn.backend.good.model.dto.GoodDto;
import kz.jarkyn.backend.good.model.dto.SellingPriceRequest;
import kz.jarkyn.backend.good.repository.AttributeRepository;
import kz.jarkyn.backend.good.repository.GoodRepository;
import kz.jarkyn.backend.good.repository.GoodAttributeRepository;
import kz.jarkyn.backend.good.repository.SellingPriceRepository;
import kz.jarkyn.backend.good.service.mapper.GoodMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
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
    public List<GoodResponse> findApiByFilter(QueryParams queryParams) {
//        Stream<GoodDto> stream = findAllDto().stream();
//        if (filter.getSearch() != null) {
//            stream = stream.filter(goodDto -> goodDto.getSearch().contains(filter.getSearch()));
//        }
//        if (filter.getGroupId() != null) {
//            stream = stream.filter(goodDto -> goodDto.getGroup().stream()
//                    .map(IdDto::getId).anyMatch(filter.getGroupId()::equals));
//        }
//        if (filter.getAttributeId() != null) {
//            stream = stream.filter(goodDto -> goodDto.getAttributes().stream()
//                    .map(IdDto::getId).anyMatch(filter.getAttributeId()::equals));
//        }
//        if (filter.getArchived() != null) {
//            stream = stream.filter(goodDto -> filter.getArchived().equals(goodDto.getArchived()));
//        }
//        return stream.map(goodMapper::toListApi).toList();
        return null;
    }

    @Transactional
    public GoodResponse createApi(GoodRequest request) {
        GoodEntity good = goodRepository.save(goodMapper.toEntity(request));
        good.setArchived(false);
        for (IdDto api : request.getAttributes()) {
            GoodAttributeEntity goodAttributeEntity = goodMapper.toEntity(good, api);
            goodAttributeRepository.save(goodAttributeEntity);
        }
        for (SellingPriceRequest sellingPrice : request.getSellingPrices()) {
            SellingPriceEntity sellingPriceEntity = goodMapper.toEntity(good, sellingPrice);
            sellingPriceRepository.save(sellingPriceEntity);
        }
        return findApiById(good.getId());
    }

    @Transactional
    public GoodResponse editApi(UUID id, GoodRequest request) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        goodMapper.editEntity(good, request);

        EntityDivider<AttributeEntity, IdDto> attributeDivider = new EntityDivider<>(
                attributeRepository.findByGood(good), request.getAttributes()
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
                sellingPriceRepository.findByGood(good), request.getSellingPrices()
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
            PrefixSearch prefixSearch = new PrefixSearch(good.getName(), good.getGroup().getName());
            result.add(goodMapper.toDto(good, attributes, sellingPrices, prefixSearch));
        }
        return result;
    }

}
