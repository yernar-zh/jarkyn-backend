package kz.jarkyn.backend.global.model.dto;

import jakarta.annotation.Nullable;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.party.model.dto.CounterpartyResponse;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import kz.jarkyn.backend.warehouse.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import org.immutables.value.Value;


@Value.Immutable
public interface DefaultLookupResponse {
    @Nullable OrganizationResponse getOrganization();
    @Nullable WarehouseResponse getWarehouse();
    @Nullable GroupDetailResponse getGroup();

    @Nullable CounterpartyResponse getSupplyCounterparty();
    @Nullable EnumTypeResponse getSupplyCurrency();

}
