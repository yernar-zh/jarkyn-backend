package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.OrganizationRequest;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import kz.jarkyn.backend.party.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Organization.PATH)
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(
            OrganizationService organizationService
    ) {
        this.organizationService = organizationService;
    }

    @GetMapping("{id}")
    public OrganizationResponse detail(@PathVariable UUID id) {
        return organizationService.findApiById(id);
    }

    @GetMapping
    public PageResponse<OrganizationResponse> list(@RequestParam Map<String, String> allParams) {
        return organizationService.findApiByFilter(QueryParams.of(allParams));
    }

    @PostMapping
    public OrganizationResponse create(@RequestBody OrganizationRequest request) {
        UUID id = organizationService.createApi(request);
        return organizationService.findApiById(id);
    }

    @PutMapping("{id}")
    public OrganizationResponse edit(@PathVariable UUID id, @RequestBody OrganizationRequest request) {
        organizationService.editApi(id, request);
        return organizationService.findApiById(id);
    }
}