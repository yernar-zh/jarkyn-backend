package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.BaseMapperConfig;
import kz.jarkyn.backend.core.mapper.RequestMapper;
import kz.jarkyn.backend.global.model.CurrencyEntity;
import kz.jarkyn.backend.party.model.AccountEntity;
import kz.jarkyn.backend.party.model.OrganizationEntity;
import kz.jarkyn.backend.party.model.dto.*;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(config = BaseMapperConfig.class)
public interface AccountMapper extends RequestMapper<AccountEntity, AccountRequest> {
    AccountResponse toResponse(AccountEntity account, BigDecimal balance);
    AccountShortResponse toResponse(OrganizationEntity organization, BigDecimal balance, CurrencyEntity currency);
}
