package kz.jarkyn.backend.document.core.service;

import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ApiValidationException;
import kz.jarkyn.backend.core.model.dto.ImmutableIdDto;
import kz.jarkyn.backend.document.core.mapper.ItemMapper;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ImmutableItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.repository.ItemRepository;
import kz.jarkyn.backend.operation.service.TurnoverService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private TurnoverService turnoverService;

    @Mock
    private AuditService auditService;

    @Mock
    private DocumentTypeService documentTypeService;

    @InjectMocks
    private ItemService itemService;

    @Test
    void saveApi_shouldThrowWhenItemBelongsToAnotherDocument() {
        DocumentEntity document = new DocumentEntity();
        document.setId(UUID.randomUUID());

        DocumentEntity anotherDocument = new DocumentEntity();
        anotherDocument.setId(UUID.randomUUID());

        UUID foreignItemId = UUID.randomUUID();
        ItemEntity foreignItem = new ItemEntity();
        foreignItem.setId(foreignItemId);
        foreignItem.setDocument(anotherDocument);

        ItemRequest request = ImmutableItemRequest.builder()
                .id(foreignItemId)
                .good(ImmutableIdDto.builder().id(UUID.randomUUID()).build())
                .price(BigDecimal.ONE)
                .quantity(1)
                .build();

        when(itemRepository.findByDocument(document)).thenReturn(List.of());
        when(itemRepository.findById(foreignItemId)).thenReturn(Optional.of(foreignItem));

        assertThatThrownBy(() -> itemService.saveApi(document, List.of(request), BigDecimal.TEN))
                .isInstanceOf(ApiValidationException.class)
                .hasMessage("Item `" + foreignItemId + "` does not belong to document `" + document.getId() + "`");

        verify(itemRepository, never()).save(any(ItemEntity.class));
        verify(itemRepository, never()).delete(any(ItemEntity.class));
        verify(turnoverService, never()).save(any(), any(), any(), any());
        verify(turnoverService, never()).delete(any(), any());
    }
}
