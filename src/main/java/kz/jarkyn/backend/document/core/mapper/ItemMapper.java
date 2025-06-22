package kz.jarkyn.backend.document.core.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.document.core.model.ItemEntity;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(config = BaseMapperConfig.class)
public interface ItemMapper extends RequestMapper<ItemEntity, ItemRequest> {
    ItemResponse toResponse(ItemEntity entity, Integer remain, BigDecimal costPrice);
}
