package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import kz.jarkyn.backend.party.model.dto.CounterpartyListResponse;
import kz.jarkyn.backend.party.model.dto.CounterpartyRequest;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.Instant;

@Mapper(config = BaseMapperConfig.class)
public interface CounterpartyMapper extends RequestResponseMapper<CounterpartyEntity, CounterpartyRequest, CounterpartyResponse> {
    @Mapping(target = "id", source = "counterparty.id")
    @Mapping(target = "name", source = "counterparty.name")
    @Mapping(target = "archived", source = "counterparty.archived")
    CounterpartyListResponse toResponse(
            CounterpartyEntity counterparty, BigDecimal accountBalance, CurrencyEntity accountCurrency,
            Instant firstSaleMoment, Instant lastSaleMoment,
            Integer totalSaleCount, BigDecimal totalSaleAmount);
}
