package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.document.core.mapper.ItemMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.operation.mode.TurnoverEntity;
import kz.jarkyn.backend.operation.mode.dto.StockResponse;
import kz.jarkyn.backend.operation.service.TurnoverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        if (document.getCommited()) {
            Map<UUID, TurnoverEntity> turnovers = turnoverService.findByDocument(document).stream()
                    .collect(Collectors.toMap(turnover -> turnover.getGood().getId(), Function.identity()));
            return items.stream().sorted(Comparator.comparing(ItemEntity::getPosition)).map(item -> {
                TurnoverEntity turnover = turnovers.get(item.getGood().getId());
                return itemMapper.toResponse(item, turnover.getRemain(), turnover.getCostPrice());
            }).toList();
        } else {
            Map<UUID, StockResponse> stocks = turnoverService.findStock(document.getWarehouse(),
                            items.stream().map(ItemEntity::getGood).toList()).stream()
                    .collect(Collectors.toMap(turnover -> turnover.getGood().getId(), Function.identity()));
            return items.stream().sorted(Comparator.comparing(ItemEntity::getPosition)).map(item -> {
                StockResponse stock = stocks.get(item.getGood().getId());
                return itemMapper.toResponse(item, stock.getRemain(), stock.getCostPrice());
            }).toList();
        }
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
    public void createPositiveTurnover(UUID id, BigDecimal costPrice) {
        ItemEntity item = itemRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        turnoverService.create(item.getDocument(), item.getGood(), item.getQuantity(), costPrice);
    }

    @Transactional
    public void createNegativeTurnover(UUID id) {
        ItemEntity item = itemRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        turnoverService.create(item.getDocument(), item.getGood(), -item.getQuantity(), null);
    }
}
