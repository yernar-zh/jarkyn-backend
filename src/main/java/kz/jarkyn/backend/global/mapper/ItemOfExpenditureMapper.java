package kz.jarkyn.backend.global.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.ResponseMapper;
import kz.jarkyn.backend.global.model.ItemOfExpenditureEntity;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapperConfig.class)
public interface ItemOfExpenditureMapper extends ResponseMapper<ItemOfExpenditureEntity, EnumTypeResponse> {
}
