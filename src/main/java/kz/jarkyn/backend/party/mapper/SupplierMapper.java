package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.Currency;
import kz.jarkyn.backend.party.model.SupplierEntity;
import kz.jarkyn.backend.party.model.dto.SupplierListResponse;
import kz.jarkyn.backend.party.model.dto.SupplierRequest;
import kz.jarkyn.backend.party.model.dto.SupplierResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(uses = EntityMapper.class)
public interface SupplierMapper extends RequestResponseMapper<SupplierEntity, SupplierRequest, SupplierResponse> {
    SupplierListResponse toResponse(
            SupplierEntity supplier, BigDecimal accountBalance, Currency accountCurrency,
            LocalDateTime firstSupplyMoment, LocalDateTime lastSupplyMoment,
            Integer totalSupplyCount, BigDecimal totalSupplyAmount);
}
