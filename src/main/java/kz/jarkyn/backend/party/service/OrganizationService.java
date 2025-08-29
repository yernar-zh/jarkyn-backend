package kz.jarkyn.backend.party.service;

import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.document.core.model.DocumentEntity;
import kz.jarkyn.backend.document.core.repository.DocumentRepository;
import kz.jarkyn.backend.document.core.specifications.DocumentSpecifications;
import kz.jarkyn.backend.party.mapper.OrganizationMapper;
import kz.jarkyn.backend.party.model.OrganizationEntity;
import kz.jarkyn.backend.party.model.OrganizationEntity_;
import kz.jarkyn.backend.party.model.dto.OrganizationRequest;
import kz.jarkyn.backend.party.model.dto.OrganizationResponse;
import kz.jarkyn.backend.party.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final AuditService auditService;
    private final DocumentRepository documentRepository;
    private final SearchFactory searchFactory;

    public OrganizationService(
            OrganizationRepository organizationRepository,
            OrganizationMapper organizationMapper,
            AuditService auditService,
            DocumentRepository documentRepository,
            SearchFactory searchFactory
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.auditService = auditService;
        this.documentRepository = documentRepository;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public OrganizationResponse findApiById(UUID id) {
        OrganizationEntity organization = organizationRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return organizationMapper.toResponse(organization);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrganizationResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<OrganizationEntity> attributes = CriteriaAttributes.<OrganizationEntity>builder()
                .add("id", (root) -> root.get(OrganizationEntity_.id))
                .add("name", (root) -> root.get(OrganizationEntity_.name))
                .add("archived", (root) -> root.get(OrganizationEntity_.archived))
                .build();
        Search<OrganizationResponse> search = searchFactory.createCriteriaSearch(
                OrganizationResponse.class, QueryParams.NAME_SEARCH, QueryParams.Sort.NAME_ASC,
                OrganizationEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(OrganizationRequest request) {
        OrganizationEntity organization = organizationRepository.save(organizationMapper.toEntity(request));
        auditService.saveEntity(organization);
        return organization.getId();
    }

    @Transactional
    public void editApi(UUID id, OrganizationRequest request) {
        OrganizationEntity organization = organizationRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        organizationMapper.editEntity(organization, request);
        auditService.saveEntity(organization);
    }


    @Transactional
    public OrganizationResponse archive(UUID id) {
        OrganizationEntity organization = organizationRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        organization.setArchived(true);
        auditService.archive(organization);
        return findApiById(id);
    }

    @Transactional
    public OrganizationResponse unarchive(UUID id) {
        OrganizationEntity organization = organizationRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        organization.setArchived(false);
        auditService.unarchive(organization);
        return findApiById(id);
    }

    @Transactional
    public void delete(UUID id) {
        OrganizationEntity organization = organizationRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        Optional<DocumentEntity> document = documentRepository.findAny(DocumentSpecifications.organization(organization));
        if (document.isPresent()) ExceptionUtils.throwRelationDeleteException();
        // remove accounts
        organizationRepository.delete(organization);
    }
}