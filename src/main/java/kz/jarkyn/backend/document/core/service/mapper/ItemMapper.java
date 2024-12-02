package kz.jarkyn.backend.document.core.service.mapper;

import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(uses = EntityMapper.class)
public abstract class ItemMapper {
    public abstract ItemResponse toResponse(ItemEntity entity);
    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "document", source = "document")
    @Mapping(target = "good", source = "request.good")
    @Mapping(target = "price", source = "request.price")
    @Mapping(target = "quantity", source = "request.quantity")
    public abstract ItemEntity toEntity(DocumentEntity document, ItemRequest request);
    public abstract void editEntity(@MappingTarget ItemEntity entity, ItemRequest request);
}
