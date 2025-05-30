package kz.jarkyn.backend.global.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.ResponseMapper;
import kz.jarkyn.backend.global.model.ItemOfExpenditureEntity;
import kz.jarkyn.backend.global.model.dto.ItemOfExpenditureResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface ItemOfExpenditureMapper extends ResponseMapper<ItemOfExpenditureEntity, ItemOfExpenditureResponse> {
}
