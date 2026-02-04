package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import kz.jarkyn.backend.party.model.dto.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface CounterpartyMapper extends RequestResponseMapper<CounterpartyEntity, CounterpartyRequest, CounterpartyListResponse> {
    CounterpartyResponse toResponse(CounterpartyEntity counterparty, List<AccountShortResponse> accounts);
}
