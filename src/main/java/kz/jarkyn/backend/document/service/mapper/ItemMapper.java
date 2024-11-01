package kz.jarkyn.backend.document.service.mapper;

import kz.jarkyn.backend.document.model.DocumentEntity;
import kz.jarkyn.backend.document.model.ItemEntity;
import kz.jarkyn.backend.document.model.dto.ItemRequest;
import kz.jarkyn.backend.document.model.dto.ItemResponse;
import kz.jarkyn.backend.core.service.mapper.EntityMapper;
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
