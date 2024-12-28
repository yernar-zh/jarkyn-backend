package kz.jarkyn.backend.counterparty.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.dto.*;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(uses = EntityMapper.class)
public interface AccountMapper extends RequestMapper<AccountEntity, AccountRequest> {
    AccountResponse toResponse(AccountEntity account, BigDecimal balance);
}
