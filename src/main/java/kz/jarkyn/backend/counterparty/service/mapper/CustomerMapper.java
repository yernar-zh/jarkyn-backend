package kz.jarkyn.backend.counterparty.service.mapper;

import kz.jarkyn.backend.core.service.mapper.EntityMapper;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.CustomerListResponse;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(uses = EntityMapper.class)
public abstract class CustomerMapper {
    public abstract CustomerResponse toResponse(CustomerEntity entity);
    public abstract CustomerEntity toEntity(CustomerRequest request);
    public abstract void editEntity(@MappingTarget CustomerEntity entity, CustomerRequest request);
    public abstract CustomerListResponse toDto(
            CustomerEntity customer,
            LocalDateTime firstSaleMoment, LocalDateTime lastSaleMoment,
            Integer totalSaleCount, Integer totalSaleAmount,
            Integer balance, PrefixSearch search);
}
