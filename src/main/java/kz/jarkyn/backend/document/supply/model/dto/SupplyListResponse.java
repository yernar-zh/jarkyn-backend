package kz.jarkyn.backend.document.supply.model.dto;

import kz.jarkyn.backend.core.model.dto.IdNamedDto;
import kz.jarkyn.backend.document.core.model.dto.DocumentResponse;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface SupplyListResponse extends DocumentResponse {
    IdNamedDto getWarehouse();
    IdNamedDto getCounterparty();
    BigDecimal getPaidAmount();
}
