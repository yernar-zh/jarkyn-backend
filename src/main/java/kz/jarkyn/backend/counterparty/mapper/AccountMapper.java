package kz.jarkyn.backend.counterparty.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.dto.*;
import org.mapstruct.Mapper;

@Mapper(uses = EntityMapper.class)
public interface AccountMapper extends RequestResponseMapper<AccountEntity, AccountRequest, AccountResponse> {
}
