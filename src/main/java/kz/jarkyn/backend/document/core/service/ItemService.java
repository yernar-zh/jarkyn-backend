package kz.jarkyn.backend.document.core.service;


import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.document.core.mapper.ItemMapper;
import kz.jarkyn.backend.core.utils.EntityDivider;
import kz.jarkyn.backend.good.model.GoodEntity;
import kz.jarkyn.backend.stock.mode.TurnoverEntity;
import kz.jarkyn.backend.stock.service.TurnoverService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
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

        Map<GoodEntity, TurnoverEntity> turnovers = turnoverService.findByDocument(document)
                .stream().collect(Collectors.toMap(TurnoverEntity::getGood, Function.identity()));
        List<GoodEntity> absentGoods = items.stream().map(ItemEntity::getGood)
                .filter(Predicate.not(turnovers::containsKey)).toList();
        Map<GoodEntity, Integer> absentGoodRemains = turnoverService.findRemain(absentGoods);

        return items.stream().map(item -> {
            TurnoverEntity turnover = turnovers.get(item.getGood());
            if (turnover != null) {
                return itemMapper.toResponse(item, turnover.getRemain(), turnover.getCostPrice());
            }
            Integer remain = absentGoodRemains.get(item.getGood());
            return itemMapper.toResponse(item, remain, 0);
        }).toList();
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
}
