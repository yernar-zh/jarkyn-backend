package kz.jarkyn.backend.service;


import kz.jarkyn.backend.model.document.common.DocumentEntity;
import kz.jarkyn.backend.model.document.common.ItemEntity;
import kz.jarkyn.backend.model.document.common.api.ItemRequest;
import kz.jarkyn.backend.model.document.common.api.ItemResponse;
import kz.jarkyn.backend.repository.ItemRepository;
import kz.jarkyn.backend.service.mapper.ItemMapper;
import kz.jarkyn.backend.utils.EntityDivider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemService(
            ItemRepository itemRepository,
            ItemMapper itemMapper
    ) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findApiByDocument(DocumentEntity document) {
        return itemRepository.findByDocument(document).stream()
                .map(itemMapper::toResponse)
                .toList();
    }

    @Transactional
    public void editApi(DocumentEntity document, List<ItemRequest> itemRequests) {
        EntityDivider<ItemEntity, ItemRequest> divider = new EntityDivider<>(
                itemRepository.findByDocument(document), itemRequests);
        for (EntityDivider<ItemEntity, ItemRequest>.Entry entry : divider.newReceived()) {
            ItemEntity item = itemMapper.toEntity(document, entry.getReceived());
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
