package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.OrganizationRequest;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import kz.jarkyn.backend.party.service.OrganizationService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

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
    public PageResponse<OrganizationResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return organizationService.findApiByFilter(QueryParams.ofMulty(allParams));
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

    @PutMapping("{id}/archive")
    public OrganizationResponse archive(@PathVariable UUID id) {
        return organizationService.archive(id);
    }

    @PutMapping("{id}/unarchive")
    public OrganizationResponse unarchive(@PathVariable UUID id) {
        return organizationService.unarchive(id);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        organizationService.delete(id);
        return MessageResponse.DELETED;
    }
}