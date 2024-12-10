package kz.jarkyn.backend.document.core.mapper;

import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.document.sale.model.SaleEntity;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(uses = EntityMapper.class)
public abstract class ItemMapper extends RequestMapper<ItemEntity, ItemRequest> {
    public abstract ItemResponse toResponse(ItemEntity entity, Integer remain, BigDecimal costPrice);
}
