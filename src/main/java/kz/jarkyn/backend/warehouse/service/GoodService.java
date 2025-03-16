
package kz.jarkyn.backend.warehouse.service;


import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.warehouse.mapper.SellingPriceMapper;
import kz.jarkyn.backend.warehouse.model.*;
import kz.jarkyn.backend.warehouse.model.dto.GoodRequest;
import kz.jarkyn.backend.warehouse.model.dto.GoodResponse;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.warehouse.model.dto.GoodListResponse;
import kz.jarkyn.backend.warehouse.model.dto.SellingPriceRequest;
import kz.jarkyn.backend.warehouse.repository.AttributeRepository;
import kz.jarkyn.backend.warehouse.repository.GoodRepository;
import kz.jarkyn.backend.warehouse.repository.GoodAttributeRepository;
import kz.jarkyn.backend.warehouse.repository.SellingPriceRepository;
import kz.jarkyn.backend.warehouse.mapper.GoodMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.operation.mode.dto.StockResponse;
import kz.jarkyn.backend.operation.service.TurnoverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GoodService {
    private final GoodRepository goodRepository;
    private final GoodAttributeRepository goodAttributeRepository;
    private final AttributeRepository attributeRepository;
    private final SellingPriceRepository sellingPriceRepository;
    private final GoodMapper goodMapper;
    private final SellingPriceMapper sellingPriceMapper;
    private final SearchFactory searchFactory;
    private final TurnoverService turnoverService;

    public GoodService(
            GoodRepository goodRepository,
            GoodAttributeRepository goodAttributeRepository,
            AttributeRepository attributeRepository,
            SellingPriceRepository sellingPriceRepository,
            GoodMapper goodMapper,
            SellingPriceMapper sellingPriceMapper,
            SearchFactory searchFactory,
            TurnoverService turnoverService) {
        this.goodRepository = goodRepository;
        this.goodAttributeRepository = goodAttributeRepository;
        this.attributeRepository = attributeRepository;
        this.sellingPriceRepository = sellingPriceRepository;
        this.goodMapper = goodMapper;
        this.sellingPriceMapper = sellingPriceMapper;
        this.searchFactory = searchFactory;
        this.turnoverService = turnoverService;
    }

    @Transactional(readOnly = true)
    public GoodResponse findApiById(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<AttributeEntity> attributes = attributeRepository.findByGood(good);
        attributes.sort(Comparator.comparing(AttributeEntity::getName));
        List<SellingPriceEntity> sellingPrices = sellingPriceRepository.findByGood(good);
        sellingPrices.sort(Comparator.comparing(SellingPriceEntity::getQuantity));
        List<StockResponse> stocks = turnoverService.findStock(good);
        return goodMapper.toResponse(good, attributes, sellingPrices, stocks);
    }

    @Transactional(readOnly = true)
    public PageResponse<GoodListResponse> findApiByFilter(QueryParams queryParams) {
        Search<GoodListResponse> search = searchFactory.createListSearch(
                GoodListResponse.class, List.of("name", "groups.name"), () ->
                        goodRepository.findAll().stream().map(good -> {
                            String path = getParentGroups(good.getGroup()).stream().map(GroupEntity::getName)
                                                  .collect(Collectors.joining("/")) + "/" + good.getName();
                            String groupIds = getParentGroups(good.getGroup()).stream().map(GroupEntity::getId)
                                    .filter(Objects::nonNull).map(UUID::toString).collect(Collectors.joining("/"));
                            String attributes = attributeRepository.findByGood(good).stream()
                                    .map(AbstractEntity::getId)
                                    .filter(Objects::nonNull).map(UUID::toString)
                                    .collect(Collectors.joining(","));
                            BigDecimal sellingPrice = sellingPriceRepository.findByGood(good)
                                    .stream().map(SellingPriceEntity::getValue)
                                    .max(BigDecimal::compareTo).orElseThrow();
                            List<StockResponse> stock = turnoverService.findStock(good);
                            Integer remind = stock.stream()
                                    .map(StockResponse::getRemain)
                                    .reduce(Integer::sum).orElseThrow();
                            BigDecimal averageCostPrice = stock.stream()
                                    .map(StockResponse::getCostPrice)
                                    .reduce(BigDecimal::add).orElseThrow()
                                    .divide(BigDecimal.valueOf(stock.size()), 2, RoundingMode.HALF_UP);
                            return goodMapper.toListResponse(good, path, groupIds, attributes, sellingPrice, remind, averageCostPrice);
                        }).toList());
        return search.getResult(queryParams);
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
            SellingPriceEntity sellingPriceEntity = sellingPriceMapper.toEntity(sellingPrice);
            sellingPriceEntity.setGood(good);
            sellingPriceRepository.save(sellingPriceEntity);
        }
        validate(good);
        searchFactory.clearCache(GoodListResponse.class);
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
            SellingPriceEntity sellingPrice = sellingPriceMapper.toEntity(entry.getReceived());
            sellingPrice.setGood(good);
            sellingPriceRepository.save(sellingPrice);
        }
        for (EntityDivider<SellingPriceEntity, SellingPriceRequest>.Entry entry : sellingPriceDivider.edited()) {
            sellingPriceMapper.editEntity(entry.getCurrent(), entry.getReceived());
        }
        sellingPriceRepository.deleteAll(sellingPriceDivider.skippedCurrent());
        validate(good);
        searchFactory.clearCache(GoodListResponse.class);
        return findApiById(id);
    }

    @Transactional
    public GoodResponse archive(UUID id, Boolean value) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        good.setArchived(value);
        searchFactory.clearCache(GoodListResponse.class);
        return findApiById(id);
    }

    private List<GroupEntity> getParentGroups(GroupEntity group) {
        ArrayList<GroupEntity> result = Stream.iterate(group, Objects::nonNull, GroupEntity::getParent)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(result);
        return result;
    }

    private void validate(GoodEntity good) {
        int size = sellingPriceRepository.findByGood(good).size();
        if (size == 0) {
            throw new DataValidationException("Необходимо указать хотя бы одну цену продажи");
        }
    }
}
