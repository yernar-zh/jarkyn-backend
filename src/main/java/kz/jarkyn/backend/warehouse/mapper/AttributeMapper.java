package kz.jarkyn.backend.warehouse.mapper;

import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.warehouse.model.dto.*;
import kz.jarkyn.backend.warehouse.model.AttributeEntity;
import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface AttributeMapper extends RequestResponseMapper<AttributeEntity, AttributeRequest, AttributeResponse> {
}
