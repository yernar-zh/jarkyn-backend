package kz.jarkyn.backend.counterparty.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.counterparty.model.Currency;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.CustomerListResponse;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerResponse;
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
