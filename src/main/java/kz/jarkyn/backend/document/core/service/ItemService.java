package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.document.core.mapper.ItemMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.operation.service.TurnoverService;
import kz.jarkyn.backend.warehouse.model.GoodEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final TurnoverService turnoverService;

    public ItemService(
            ItemRepository itemRepository,
            ItemMapper itemMapper,
            TurnoverService turnoverService) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.turnoverService = turnoverService;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findApiByDocument(DocumentEntity document) {
        List<ItemEntity> items = itemRepository.findByDocument(document);
        List<Pair<WarehouseEntity, GoodEntity>> goodsPair = items.stream()
                .map(itemEntity -> Pair.of(document.getWarehouse(), itemEntity.getGood()))
                .toList();
        TurnoverService.StockDto stock = turnoverService.findStockAtMoment(goodsPair, document.getMoment()).getFirst();
        return items.stream().sorted(Comparator.comparing(ItemEntity::getPosition))
                .map(item -> itemMapper.toResponse(item, stock.getRemain(), stock.getCostPrice()))
                .toList();
    }

    @Transactional
    public void saveApi(DocumentEntity document, List<ItemRequest> itemRequests) {
        EntityDivider<ItemEntity, ItemRequest> divider = new EntityDivider<>(
                itemRepository.findByDocument(document), itemRequests);
        for (EntityDivider<ItemEntity, ItemRequest>.Entry entry : divider.newReceived()) {
            ItemEntity item = itemMapper.toEntity(entry.getReceived());
            item.setDocument(document);
            item.setPosition(entry.getReceivedPosition());
            itemRepository.save(item);
        }
        for (EntityDivider<ItemEntity, ItemRequest>.Entry entry : divider.edited()) {
            itemMapper.editEntity(entry.getCurrent(), entry.getReceived());
            entry.getCurrent().setPosition(entry.getReceivedPosition());
        }
        itemRepository.deleteAll(divider.skippedCurrent());
    }

    @Transactional
    public void createPositiveTurnover(DocumentEntity document, BigDecimal documentCostPrice) {
        List<ItemEntity> items = itemRepository.findByDocument(document);
        BigDecimal totalItemAmount = items.stream()
                .map(item -> BigDecimal.valueOf(item.getQuantity()).multiply(item.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<GoodEntity, List<ItemEntity>> map = items.stream().collect(Collectors.groupingBy(ItemEntity::getGood));
        for (Map.Entry<GoodEntity, List<ItemEntity>> entry : map.entrySet()) {
            Integer quantity = entry.getValue().stream().map(ItemEntity::getQuantity).reduce(0, Integer::sum);
            BigDecimal price = entry.getValue().stream()
                    .map(itemEntity -> itemEntity.getPrice().multiply(BigDecimal.valueOf(quantity)))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(quantity), 2, RoundingMode.HALF_UP);
            BigDecimal costPricePerUnit = documentCostPrice.multiply(price).divide(totalItemAmount, 2, RoundingMode.HALF_UP);
            turnoverService.create(document, entry.getKey(), quantity, costPricePerUnit);
        }
    }

    @Transactional
    public void createNegativeTurnover(DocumentEntity document) {
        List<ItemEntity> items = itemRepository.findByDocument(document);
        for (ItemEntity item : items) {
            turnoverService.create(item.getDocument(), item.getGood(), -item.getQuantity(), BigDecimal.ZERO);
        }
    }

    @Transactional
    public void deleteTurnover(DocumentEntity document) {
        turnoverService.delete(document);
    }
}
