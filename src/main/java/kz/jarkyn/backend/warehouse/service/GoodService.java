
package kz.jarkyn.backend.warehouse.service;


import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.exception.DataValidationException;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.AbstractEntity;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.document.core.model.DocumentSearchEntity;
import kz.jarkyn.backend.document.core.model.DocumentSearchEntity_;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.document.core.specifications.ItemSpecifications;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.mode.TurnoverEntity_;
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

        if (filterMoment == null) throw new ApiValidationException("Filed `filterMoment` is null");

        CriteriaAttributes.Builder<GoodEntity> attributesBuilder = CriteriaAttributes
                .<GoodEntity>builder()
                .add("id", (root) -> root.get(GoodEntity_.id))
                .add("name", (root) -> root.get(GoodEntity_.name))
                .add("archived", (root) -> root.get(GoodEntity_.archived))
                .addReference("group", (root) -> root.get(GoodEntity_.group))
                .add("minimumPrice", (root) -> root.get(GoodEntity_.minimumPrice))
                .add("weight", (root) -> root.get(GoodEntity_.weight))
                .add("path", (root) -> root.get(GoodEntity_.path))
                .add("groupIds", (root) -> root.get(GoodEntity_.groupIds))
                .add("attributeIds", (root) -> root.get(GoodEntity_.attributeIds))
                .add("sellingPrice", (root, query, cb, map) -> {
                    Subquery<BigDecimal> subQuery = query.subquery(BigDecimal.class);
                    Root<SellingPriceEntity> sellingPriceRoot = subQuery.from(SellingPriceEntity.class);
                    subQuery.select(cb.coalesce(cb.max(sellingPriceRoot.get(SellingPriceEntity_.value)), BigDecimal.ZERO));
                    subQuery.where(cb.equal(sellingPriceRoot.get(SellingPriceEntity_.good), root));
                    return subQuery;
                })
                .add("remain", (root, query, cb, map) -> {
                    Subquery<Integer> subquery = query.subquery(Integer.class);
                    Root<TurnoverEntity> turnoverRoot = subquery.from(TurnoverEntity.class);

                    Subquery<Instant> innerSubquery = query.subquery(Instant.class);
                    Root<TurnoverEntity> innerTurnoverRoot = innerSubquery.from(TurnoverEntity.class);
                    innerSubquery.select(cb.greatest(innerTurnoverRoot.get(TurnoverEntity_.moment)));
                    innerSubquery.where(
                            cb.equal(innerTurnoverRoot.get(TurnoverEntity_.good), root),
                            cb.equal(innerTurnoverRoot.get(TurnoverEntity_.warehouse), turnoverRoot.get(TurnoverEntity_.warehouse)),
                            cb.lessThan(innerTurnoverRoot.get(TurnoverEntity_.moment), filterMoment)
                    );

                    subquery.where(
                            cb.equal(turnoverRoot.get(TurnoverEntity_.good), root),
                            turnoverRoot.get(TurnoverEntity_.warehouse).in(warehouses),
                            cb.equal(turnoverRoot.get(TurnoverEntity_.moment), innerSubquery)
                    );

                    subquery.select(cb.coalesce(cb.sum(
                            cb.sum(turnoverRoot.get(TurnoverEntity_.remain), turnoverRoot.get(TurnoverEntity_.quantity))
                    ), 0));
                    return subquery;
                })
                .add("costPrice", (root, query, cb, map) -> {
                    Subquery<BigDecimal> subquery = query.subquery(BigDecimal.class);
                    Root<TurnoverEntity> turnoverRoot = subquery.from(TurnoverEntity.class);

                    Subquery<Instant> innerSubquery = query.subquery(Instant.class);
                    Root<TurnoverEntity> innerTurnoverRoot = innerSubquery.from(TurnoverEntity.class);
                    innerSubquery.select(cb.greatest(innerTurnoverRoot.get(TurnoverEntity_.moment)));
                    innerSubquery.where(
                            cb.equal(innerTurnoverRoot.get(TurnoverEntity_.good), root),
                            cb.equal(innerTurnoverRoot.get(TurnoverEntity_.warehouse), turnoverRoot.get(TurnoverEntity_.warehouse)),
                            cb.lessThan(innerTurnoverRoot.get(TurnoverEntity_.moment), filterMoment)
                    );

                    subquery.where(
                            cb.equal(turnoverRoot.get(TurnoverEntity_.good), root),
                            turnoverRoot.get(TurnoverEntity_.warehouse).in(warehouses),
                            cb.equal(turnoverRoot.get(TurnoverEntity_.moment), innerSubquery)
                    );

                    subquery.select(cb.coalesce(cb.max(turnoverRoot.get(TurnoverEntity_.costPricePerUnit)), BigDecimal.ZERO));
                    return subquery;
                });

        Search<GoodListResponse> search = searchFactory.createCriteriaSearch(
                GoodListResponse.class, List.of("search"), QueryParams.Sort.NAME_ASC,
                GoodEntity.class, attributesBuilder.build());
        return search.getResult(queryParams);
    }

    @Transactional
    public GoodResponse createApi(GoodRequest request) {
        GoodEntity good = goodRepository.save(goodMapper.toEntity(request));
        good.setName(good.getName().replaceAll("\\s+", " ").trim());
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
        auditService.saveEntity(good);
        validateAndFill(good);
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
        validateAndFill(good);
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

    private void validateAndFill(GoodEntity good) {
        int size = sellingPriceRepository.findByGood(good).size();
        if (size == 0) {
            throw new DataValidationException("Необходимо указать хотя бы одну цену продажи");
        }

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
        String search = good.getName() + " " + good.getSearchKeywords() + " " +
                good.getGroup().getName() + " " + good.getGroup().getSearchKeywords() + " " + attributeSearchKeywords;
        good.setSearch(search);
        good.setPath(path);
        good.setGroupIds(groupIds);
        good.setAttributeIds(attributeIds);
    }
}
