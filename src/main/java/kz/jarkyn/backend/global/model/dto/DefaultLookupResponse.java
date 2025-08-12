package kz.jarkyn.backend.global.model.dto;

import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import kz.jarkyn.backend.warehouse.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import org.immutables.value.Value;


@Value.Immutable
public interface DefaultLookupResponse {
    OrganizationResponse getOrganization();
    WarehouseResponse getWarehouse();
    GroupDetailResponse getGroup();

    CounterpartyResponse getSupplyCounterparty();
    EnumTypeResponse getSupplyCurrency();

}
