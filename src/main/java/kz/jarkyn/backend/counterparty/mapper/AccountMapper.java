package kz.jarkyn.backend.counterparty.mapper;

import kz.jarkyn.backend.core.service.mapper.EntityMapper;
import kz.jarkyn.backend.core.service.mapper.RequestResponseMapper;
import kz.jarkyn.backend.core.utils.PrefixSearch;
import kz.jarkyn.backend.counterparty.model.AccountEntity;
import kz.jarkyn.backend.counterparty.model.CustomerEntity;
import kz.jarkyn.backend.counterparty.model.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;

@Mapper(uses = EntityMapper.class)
public abstract class AccountMapper extends RequestResponseMapper<AccountEntity, AccountRequest, AccountResponse> {
}
