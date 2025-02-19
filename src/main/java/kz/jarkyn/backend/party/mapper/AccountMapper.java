package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.dto.*;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(uses = EntityMapper.class)
public interface AccountMapper extends RequestMapper<AccountEntity, AccountRequest> {
    AccountResponse toResponse(AccountEntity account, BigDecimal balance);
}
