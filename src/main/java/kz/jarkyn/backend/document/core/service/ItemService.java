package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.document.core.mapper.ItemMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.operation.service.TurnoverService;
import kz.jarkyn.backend.good.model.GoodEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final TurnoverService turnoverService;
    private final AuditService auditService;
    private final DocumentTypeService documentTypeService;

    public ItemService(
            ItemRepository itemRepository,
            ItemMapper itemMapper,
            TurnoverService turnoverService,
            AuditService auditService, DocumentTypeService documentTypeService) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.turnoverService = turnoverService;
        this.auditService = auditService;
        this.documentTypeService = documentTypeService;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findApiByDocument(DocumentEntity document) {
        List<ItemEntity> items = itemRepository.findByDocument(document);
        Map<GoodEntity, TurnoverService.StockDto> stocks = turnoverService
                .findStockByDocument(document)
                .stream().collect(Collectors.toMap(TurnoverService.StockDto::getGood, Function.identity()));
        return items.stream().sorted(Comparator.comparing(ItemEntity::getPosition))
                .map(item -> itemMapper.toResponse(item, stocks.get(item.getGood()).getRemain(),
                        stocks.get(item.getGood()).getCostPrice()))
                .toList();
    }


    @Transactional
    public void saveApi(DocumentEntity document, List<ItemRequest> items, BigDecimal documentCostPrice) {
        List<ItemEntity> currentItems = itemRepository.findByDocument(document);
        validateItemOwnership(document, currentItems, items);
        EntityDivider<ItemEntity, ItemRequest> divider = new EntityDivider<>(currentItems, items);
        for (EntityDivider<ItemEntity, ItemRequest>.Entry entry : divider.newReceived()) {
            ItemEntity item = itemMapper.toEntity(entry.getReceived());
            item.setDocument(document);
            item.setPosition(entry.getReceivedPosition());
            itemRepository.save(item);
            auditService.saveEntityAsync(item, item.getDocument(), "items");
        }
        for (EntityDivider<ItemEntity, ItemRequest>.Entry entry : divider.edited()) {
            itemMapper.editEntity(entry.getCurrent(), entry.getReceived());
            entry.getCurrent().setPosition(entry.getReceivedPosition());
            auditService.saveEntityAsync(entry.getCurrent(), entry.getCurrent().getDocument(), "items");
        }
        for (ItemEntity item : divider.skippedCurrent()) {
            itemRepository.delete(item);
            auditService.delete(item, item.getDocument());
        }

        currentItems = itemRepository.findByDocument(document);
        recalculateTurnover(document, currentItems, documentCostPrice);
    }

    @Transactional
    public void recalculateTurnover(DocumentEntity document, BigDecimal documentCostPrice) {
        List<ItemEntity> currentItems = itemRepository.findByDocument(document);
        recalculateTurnover(document, currentItems, documentCostPrice);
    }

    private void recalculateTurnover(
            DocumentEntity document, List<ItemEntity> currentItems, BigDecimal documentCostPrice) {
        if (!document.getCommited()) {
            turnoverService.delete(document, Set.of());
            return;
        }

        Map<GoodEntity, List<ItemEntity>> itemMap = currentItems.stream()
                .collect(Collectors.groupingBy(ItemEntity::getGood));
        Set<GoodEntity> updatedGoods = new HashSet<>();
        boolean positiveTurnover = documentTypeService.isSupply(document.getType());

        if (positiveTurnover) {
            BigDecimal totalItemAmount = currentItems.stream()
                    .map(item -> BigDecimal.valueOf(item.getQuantity()).multiply(item.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            for (Map.Entry<GoodEntity, List<ItemEntity>> entry : itemMap.entrySet()) {
                Integer quantity = entry.getValue().stream().map(ItemEntity::getQuantity).reduce(0, Integer::sum);
                if (quantity == 0) {
                    continue;
                }
                BigDecimal goodAmount = entry.getValue().stream()
                        .map(itemEntity -> itemEntity.getPrice().multiply(BigDecimal.valueOf(itemEntity.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal costPricePerUnit = totalItemAmount.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : documentCostPrice.multiply(goodAmount)
                        .divide(totalItemAmount, 8, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);
                turnoverService.save(document, entry.getKey(), quantity, costPricePerUnit);
                updatedGoods.add(entry.getKey());
            }
        } else {
            for (Map.Entry<GoodEntity, List<ItemEntity>> entry : itemMap.entrySet()) {
                Integer quantity = entry.getValue().stream().map(ItemEntity::getQuantity).reduce(0, Integer::sum);
                if (quantity == 0) {
                    continue;
                }
                turnoverService.save(document, entry.getKey(), -quantity, BigDecimal.ZERO);
                updatedGoods.add(entry.getKey());
            }
        }
        turnoverService.delete(document, updatedGoods);
    }

    private void validateItemOwnership(DocumentEntity document, List<ItemEntity> currentItems, List<ItemRequest> items) {
        Set<UUID> currentItemIds = currentItems.stream()
                .map(ItemEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (ItemRequest item : items) {
            UUID itemId = item.getId();
            if (itemId == null || currentItemIds.contains(itemId)) {
                continue;
            }
            ItemEntity existingItem = itemRepository.findById(itemId).orElse(null);
            if (existingItem != null && !Objects.equals(existingItem.getDocument(), document)) {
                throw new ApiValidationException("Item `" + itemId + "` does not belong to document `" + document.getId() + "`");
            }
        }
    }

    @Transactional
    public void createNegativeTurnover(DocumentEntity document) {
        List<ItemEntity> items = itemRepository.findByDocument(document);
        for (ItemEntity item : items) {
            turnoverService.save(item.getDocument(), item.getGood(), -item.getQuantity(), BigDecimal.ZERO);
        }
    }

    @Transactional
    public void deleteTurnover(DocumentEntity document) {
        turnoverService.delete(document, Set.of());
    }
}
