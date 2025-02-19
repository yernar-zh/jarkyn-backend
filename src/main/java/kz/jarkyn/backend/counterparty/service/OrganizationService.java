package kz.jarkyn.backend.counterparty.service;

import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.counterparty.mapper.OrganizationMapper;
import kz.jarkyn.backend.counterparty.model.OrganizationEntity;
import kz.jarkyn.backend.counterparty.model.dto.OrganizationRequest;
import kz.jarkyn.backend.counterparty.model.dto.OrganizationResponse;
import kz.jarkyn.backend.counterparty.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationMapper organizationMapper,
            AuditService auditService,
            SearchFactory searchFactory
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public OrganizationResponse findApiById(UUID id) {
        OrganizationEntity organization = organizationRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return organizationMapper.toResponse(organization);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrganizationResponse> findApiByFilter(QueryParams queryParams) {
        Search<OrganizationResponse> search = searchFactory.createListSearch(
                OrganizationResponse.class, List.of(), () -> organizationMapper.toResponse(organizationRepository.findAll()));
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(OrganizationRequest request) {
        OrganizationEntity organization = organizationRepository.save(organizationMapper.toEntity(request));
        auditService.saveChanges(organization);
        return organization.getId();
    }

    @Transactional
    public void editApi(UUID id, OrganizationRequest request) {
        OrganizationEntity organization = organizationRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        organizationMapper.editEntity(organization, request);
        auditService.saveChanges(organization);
    }
}