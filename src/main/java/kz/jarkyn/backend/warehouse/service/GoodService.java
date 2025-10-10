
package kz.jarkyn.backend.warehouse.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.document.core.service.ItemService;
import kz.jarkyn.backend.document.core.specifications.ItemSpecifications;
import kz.jarkyn.backend.warehouse.mapper.SellingPriceMapper;
import kz.jarkyn.backend.warehouse.model.*;
import kz.jarkyn.backend.warehouse.model.dto.*;
import kz.jarkyn.backend.core.model.dto.IdDto;
import kz.jarkyn.backend.warehouse.repository.*;
import kz.jarkyn.backend.warehouse.mapper.GoodMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.operation.service.TurnoverService;
import kz.jarkyn.backend.warehouse.specifications.GoodAttributeSpecifications;
import kz.jarkyn.backend.warehouse.specifications.SellingPriceSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
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
    private final WarehouseRepository warehouseRepository;
    private final ItemRepository itemRepository;
    private final AuditService auditService;

    public GoodService(
            GoodRepository goodRepository,
            GoodAttributeRepository goodAttributeRepository,
            AttributeRepository attributeRepository,
            SellingPriceRepository sellingPriceRepository,
            GoodMapper goodMapper,
            SellingPriceMapper sellingPriceMapper,
            SearchFactory searchFactory,
            TurnoverService turnoverService,
            WarehouseRepository warehouseRepository,
            ItemRepository itemRepository, AuditService auditService) {
        this.goodRepository = goodRepository;
        this.goodAttributeRepository = goodAttributeRepository;
        this.attributeRepository = attributeRepository;
        this.sellingPriceRepository = sellingPriceRepository;
        this.goodMapper = goodMapper;
        this.sellingPriceMapper = sellingPriceMapper;
        this.searchFactory = searchFactory;
        this.turnoverService = turnoverService;
        this.warehouseRepository = warehouseRepository;
        this.itemRepository = itemRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public GoodResponse findApiById(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<AttributeEntity> attributes = attributeRepository.findByGood(good);
        attributes.sort(Comparator.comparing(AttributeEntity::getName));
        List<SellingPriceEntity> sellingPrices = sellingPriceRepository.findByGood(good);
        sellingPrices.sort(Comparator.comparing(SellingPriceEntity::getQuantity));
        List<WarehouseEntity> warehouses = warehouseRepository.findByArchived(Boolean.FALSE);
        List<Pair<WarehouseEntity, GoodEntity>> goodPair = warehouses.stream()
                .map(warehouse -> Pair.of(warehouse, good)).toList();
        List<TurnoverService.StockDto> stocks = turnoverService.findStockAtMoment(goodPair, Instant.now());
        return goodMapper.toResponse(good, attributes, sellingPrices, stocks);
    }

    @Transactional(readOnly = true)
    public PageResponse<GoodListResponse> findApiByFilter(
            QueryParams queryParams,
            List<UUID> filterWarehouseIds, Instant filterMoment
    ) {
        List<WarehouseEntity> warehouses = filterWarehouseIds.isEmpty()
                ? warehouseRepository.findByArchived(false)
                : warehouseRepository.findAllById(filterWarehouseIds);
        Search<GoodListResponse> search = searchFactory.createListSearch(
                GoodListResponse.class,
                List.of("name", "searchKeywords", "group.name", "group.searchKeywords", "attributeSearchKeywords"),
                new QueryParams.Sort("path", QueryParams.Sort.Type.ASC),
                () -> goodRepository.findAll().stream().map(good -> {
                    String path = getParentGroups(good.getGroup()).stream().map(GroupEntity::getName)
                            .collect(Collectors.joining("/")) + "/" + good.getName();
                    String groupIds = getParentGroups(good.getGroup()).stream().map(GroupEntity::getId)
                            .filter(Objects::nonNull).map(UUID::toString).collect(Collectors.joining("/"));
                    List<AttributeEntity> attributes = attributeRepository.findByGood(good);
                    String attributeIds = attributes.stream()
                            .map(AbstractEntity::getId)
                            .filter(Objects::nonNull).map(UUID::toString)
                            .collect(Collectors.joining(","));
                    String attributeSearchKeywords = attributes.stream()
                            .flatMap(attribute -> Stream.of(attribute.getName(), attribute.getSearchKeywords()))
                            .collect(Collectors.joining(" "));
                    BigDecimal sellingPrice = sellingPriceRepository.findByGood(good)
                            .stream().map(SellingPriceEntity::getValue)
                            .max(BigDecimal::compareTo).orElseThrow();
                    List<Pair<WarehouseEntity, GoodEntity>> goodPair = warehouses.stream()
                            .map(warehouse -> Pair.of(warehouse, good)).toList();
                    List<TurnoverService.StockDto> stocks = turnoverService.findStockAtMoment(
                            goodPair, Optional.ofNullable(filterMoment).orElseGet(Instant::now));
                    Integer remind = stocks.stream().map(TurnoverService.StockDto::getRemain)
                            .reduce(0, Integer::sum);
                    BigDecimal costPrice = stocks.stream().map(TurnoverService.StockDto::getCostPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return goodMapper.toListResponse(good, path, groupIds, attributeIds, attributeSearchKeywords,
                            sellingPrice, remind, costPrice);
                }).toList());
        return search.getResult(queryParams);
    }

    @Transactional
    public GoodResponse createApi(GoodRequest request) {
        GoodEntity good = goodRepository.save(goodMapper.toEntity(request));
        good.setArchived(false);
        for (IdDto dto : request.getAttributes()) {
            GoodAttributeEntity goodAttribute = goodMapper.toEntity(good, dto);
            goodAttributeRepository.save(goodAttribute);
            auditService.saveEntity(goodAttribute, good, "attributes");
        }
        for (SellingPriceRequest sellingPriceRequest : request.getSellingPrices()) {
            SellingPriceEntity sellingPrice = sellingPriceMapper.toEntity(sellingPriceRequest);
            sellingPrice.setGood(good);
            sellingPriceRepository.save(sellingPrice);
            auditService.saveEntity(sellingPrice, good, "sellingPrices");
        }
        validate(good);
        auditService.saveEntity(good);
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
            GoodAttributeEntity goodAttribute = goodMapper.toEntity(good, entry.getReceived());
            goodAttributeRepository.save(goodAttribute);
            auditService.saveEntity(goodAttribute, good, "attributes");
        }
        for (AttributeEntity attribute : attributeDivider.skippedCurrent()) {
            GoodAttributeEntity goodAttribute = goodAttributeRepository
                    .findByGoodAndAttribute(good, attribute).orElseThrow();
            goodAttributeRepository.delete(goodAttribute);
            auditService.delete(goodAttribute, good);
        }

        EntityDivider<SellingPriceEntity, SellingPriceRequest> sellingPriceDivider = new EntityDivider<>(
                sellingPriceRepository.findByGood(good), request.getSellingPrices()
        );
        for (EntityDivider<SellingPriceEntity, SellingPriceRequest>.Entry entry : sellingPriceDivider.newReceived()) {
            SellingPriceEntity sellingPrice = sellingPriceMapper.toEntity(entry.getReceived());
            sellingPrice.setGood(good);
            sellingPriceRepository.save(sellingPrice);
            auditService.saveEntity(sellingPrice, good, "sellingPrices");
        }
        for (EntityDivider<SellingPriceEntity, SellingPriceRequest>.Entry entry : sellingPriceDivider.edited()) {
            sellingPriceMapper.editEntity(entry.getCurrent(), entry.getReceived());
            auditService.saveEntity(entry.getCurrent(), good, "sellingPrices");
        }
        for (SellingPriceEntity sellingPrice : sellingPriceDivider.skippedCurrent()) {
            sellingPriceRepository.delete(sellingPrice);
            auditService.delete(sellingPrice, good);
        }
        validate(good);
        auditService.saveEntity(good);
        return findApiById(id);
    }

    @Transactional
    public GoodResponse archive(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        good.setArchived(true);
        auditService.archive(good);
        return findApiById(id);
    }

    @Transactional
    public GoodResponse unarchive(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        good.setArchived(false);
        auditService.unarchive(good);
        return findApiById(id);
    }

    @Transactional
    public void delete(UUID id) {
        GoodEntity good = goodRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        List<ItemEntity> items = itemRepository.findAll(Specification.where(ItemSpecifications.good(good)));
        if (!items.isEmpty()) ExceptionUtils.throwRelationDeleteException();
        sellingPriceRepository.deleteAll(sellingPriceRepository.findAll(Specification
                .where(SellingPriceSpecifications.good(good))));
        goodAttributeRepository.deleteAll(goodAttributeRepository.findAll(Specification
                .where(GoodAttributeSpecifications.good(good))));
        goodRepository.delete(good);
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
