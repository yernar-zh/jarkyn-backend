package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.Currency;
import kz.jarkyn.backend.party.model.CustomerEntity;
import kz.jarkyn.backend.party.model.dto.CustomerListResponse;
import kz.jarkyn.backend.party.model.dto.CustomerRequest;
import kz.jarkyn.backend.party.model.dto.CustomerResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(uses = EntityMapper.class)
public interface CustomerMapper extends RequestResponseMapper<CustomerEntity, CustomerRequest, CustomerResponse> {
    CustomerListResponse toResponse(
            CustomerEntity customer, BigDecimal accountBalance, Currency accountCurrency,
            LocalDateTime firstSaleMoment, LocalDateTime lastSaleMoment,
            Integer totalSaleCount, BigDecimal totalSaleAmount);
}
