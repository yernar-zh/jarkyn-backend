package kz.jarkyn.backend.global.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.ResponseMapper;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface CurrencyMapper extends ResponseMapper<CurrencyEntity, EnumTypeResponse> {
}
