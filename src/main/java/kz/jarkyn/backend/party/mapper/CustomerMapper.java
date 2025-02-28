package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.CustomerEntity;
import kz.jarkyn.backend.party.model.dto.CustomerListResponse;
import kz.jarkyn.backend.party.model.dto.CustomerRequest;
import kz.jarkyn.backend.party.model.dto.CustomerResponse;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(uses = EntityMapper.class)
public interface CustomerMapper extends RequestResponseMapper<CustomerEntity, CustomerRequest, CustomerResponse> {
    @Mapping(target = "id", source = "customer.id")
    @Mapping(target = "name", source = "customer.name")
    @Mapping(target = "archived", source = "customer.archived")
    CustomerListResponse toResponse(
            CustomerEntity customer, BigDecimal accountBalance, CurrencyEntity accountCurrency,
            LocalDateTime firstSaleMoment, LocalDateTime lastSaleMoment,
            Integer totalSaleCount, BigDecimal totalSaleAmount);
}
