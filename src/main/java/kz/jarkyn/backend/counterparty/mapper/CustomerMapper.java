package kz.jarkyn.backend.counterparty.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.CustomerRequest;
import kz.jarkyn.backend.counterparty.model.dto.CustomerResponse;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public abstract class CustomerMapper
        extends RequestResponseMapper<CustomerEntity, CustomerRequest, CustomerResponse> {
}
