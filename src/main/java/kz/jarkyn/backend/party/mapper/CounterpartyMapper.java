package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import kz.jarkyn.backend.party.model.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(config = BaseMapperConfig.class)
public interface CounterpartyMapper extends RequestMapper<CounterpartyEntity, CounterpartyRequest> {
    @Mapping(target = "id", source = "counterparty.id")
    @Mapping(target = "name", source = "counterparty.name")
    @Mapping(target = "archived", source = "counterparty.archived")
    CounterpartyListResponse toResponse(
            CounterpartyEntity counterparty, BigDecimal accountBalance, EnumTypeResponse accountCurrency);
    CounterpartyResponse toResponse(CounterpartyEntity counterparty, List<AccountShortResponse> accounts);
}
