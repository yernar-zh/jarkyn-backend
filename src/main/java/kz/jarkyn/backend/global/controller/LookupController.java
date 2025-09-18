package kz.jarkyn.backend.global.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.EnumTypeResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.global.mapper.LookupMapper;
import kz.jarkyn.backend.global.model.dto.DefaultLookupResponse;
import kz.jarkyn.backend.global.model.dto.ImmutableDefaultLookupResponse;
import kz.jarkyn.backend.global.service.CurrencyService;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import kz.jarkyn.backend.party.service.CounterpartyService;
import kz.jarkyn.backend.party.service.OrganizationService;
import kz.jarkyn.backend.warehouse.model.dto.GroupDetailResponse;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import kz.jarkyn.backend.warehouse.service.GroupService;
import kz.jarkyn.backend.warehouse.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Lookup.PATH)
public class LookupController {
    private final OrganizationService organizationService;
    private final WarehouseService warehouseService;
    private final CounterpartyService counterpartyService;
    private final CurrencyService currencyService;
    private final GroupService groupService;
    private final LookupMapper lookupMapper;

    public LookupController(
            OrganizationService organizationService,
            WarehouseService warehouseService,
            CounterpartyService counterpartyService,
            CurrencyService currencyService,
            GroupService groupService,
            LookupMapper lookupMapper) {
        this.organizationService = organizationService;
        this.warehouseService = warehouseService;
        this.counterpartyService = counterpartyService;
        this.currencyService = currencyService;
        this.groupService = groupService;
        this.lookupMapper = lookupMapper;
    }

    @GetMapping("default")
    public DefaultLookupResponse getDefault() {
        OrganizationResponse organization = organizationService
                .findApiByFilter(QueryParams.of(Map.of("archived", "false")))
                .getRow().stream().findAny().orElse(null);
        WarehouseResponse warehouse = warehouseService
                .findApiByFilter(QueryParams.of(Map.of("archived", "false")))
                .getRow().stream().findAny().orElse(null);
        EnumTypeResponse supplyCurrency = currencyService
                .findApiById(UUID.fromString("e6a3c207-a358-47bf-ac18-2d09973f3807"));
        return lookupMapper.toDefaultResponse(organization, warehouse, null, supplyCurrency, null);
    }
}