package kz.jarkyn.backend.good.mapper;

import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.good.model.dto.*;
import kz.jarkyn.backend.good.model.AttributeEntity;
import kz.jarkyn.backend.core.mapper.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface AttributeMapper extends RequestResponseMapper<AttributeEntity, AttributeRequest, AttributeResponse> {
}
