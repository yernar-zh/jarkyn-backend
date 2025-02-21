package kz.jarkyn.backend.global.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.ResponseMapper;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.global.model.dto.CurrencyResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface CurrencyMapper extends ResponseMapper<CurrencyEntity, CurrencyResponse> {
}
