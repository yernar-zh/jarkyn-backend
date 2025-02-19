package kz.jarkyn.backend.party.mapper;

import kz.jarkyn.backend.core.mapper.EntityMapper;
import kz.jarkyn.backend.core.mapper.RequestResponseMapper;
import kz.jarkyn.backend.party.model.SupplierEntity;
import kz.jarkyn.backend.party.model.dto.SupplierListResponse;
import kz.jarkyn.backend.party.model.dto.SupplierRequest;
import kz.jarkyn.backend.party.model.dto.SupplierResponse;
import kz.jarkyn.backend.reference.model.CurrencyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(uses = EntityMapper.class)
public interface SupplierMapper extends RequestResponseMapper<SupplierEntity, SupplierRequest, SupplierResponse> {
    @Mapping(target = "id", source = "supplier.id")
    @Mapping(target = "name", source = "supplier.name")
    SupplierListResponse toResponse(
            SupplierEntity supplier, BigDecimal accountBalance, CurrencyEntity accountCurrency,
            LocalDateTime firstSupplyMoment, LocalDateTime lastSupplyMoment,
            Integer totalSupplyCount, BigDecimal totalSupplyAmount);
}
