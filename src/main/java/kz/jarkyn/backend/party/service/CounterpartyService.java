
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
import kz.jarkyn.backend.party.model.CounterpartyEntity;
import kz.jarkyn.backend.party.model.dto.*;
import kz.jarkyn.backend.party.repository.CounterpartyRepository;
import kz.jarkyn.backend.party.mapper.CounterpartyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CounterpartyService {
    private final CounterpartyRepository counterpartyRepository;
    private final CounterpartyMapper counterpartyMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;
    private final AccountService accountService;
    private final DocumentRepository documentRepository;

    public CounterpartyService(
            CounterpartyRepository counterpartyRepository,
            CounterpartyMapper counterpartyMapper,
            AuditService auditService,
            SearchFactory searchFactory,
            AccountService accountService,
            DocumentRepository documentRepository) {
        this.counterpartyRepository = counterpartyRepository;
        this.counterpartyMapper = counterpartyMapper;
        this.auditService = auditService;
        this.searchFactory = searchFactory;
        this.accountService = accountService;
        this.documentRepository = documentRepository;
    }

    @Transactional(readOnly = true)
    public CounterpartyResponse findApiById(UUID id) {
        CounterpartyEntity counterparty = counterpartyRepository.findById(id)
                .orElseThrow(ExceptionUtils.entityNotFound());
        List<AccountShortResponse> accounts = accountService.findBalanceByCounterparty(counterparty);
        return counterpartyMapper.toResponse(counterparty, accounts);
    }

    @Transactional(readOnly = true)
    public PageResponse<CounterpartyListResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<CounterpartyEntity> attributes = CriteriaAttributes.<CounterpartyEntity>builder()
                .add("id", (root) -> root.get("id"))
                .add("name", (root) -> root.get("name"))
                .add("archived", (root) -> root.get("archived"))
                .add("phoneNumber", (root) -> root.get("phoneNumber"))
                .add("discount", (root) -> root.get("discount"))
                .add("shippingAddress", (root) -> root.get("shippingAddress"))
                .build();
        Search<CounterpartyListResponse> search = searchFactory.createCriteriaSearch(
                CounterpartyListResponse.class, List.of("name", "phoneNumber"), QueryParams.Sort.NAME_ASC,
                CounterpartyEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(CounterpartyRequest request) {
        CounterpartyEntity counterparty = counterpartyRepository.save(counterpartyMapper.toEntity(request));
        auditService.saveEntity(counterparty);
        return counterparty.getId();
    }

    @Transactional
    public void editApi(UUID id, CounterpartyRequest request) {
        CounterpartyEntity counterparty = counterpartyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        counterpartyMapper.editEntity(counterparty, request);
        auditService.saveEntity(counterparty);
    }

    @Transactional
    public CounterpartyResponse archive(UUID id) {
        CounterpartyEntity counterparty = counterpartyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        counterparty.setArchived(true);
        auditService.archive(counterparty);
        return findApiById(id);
    }

    @Transactional
    public CounterpartyResponse unarchive(UUID id) {
        CounterpartyEntity counterparty = counterpartyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        counterparty.setArchived(false);
        auditService.unarchive(counterparty);
        return findApiById(id);
    }

    @Transactional
    public void delete(UUID id) {
        CounterpartyEntity counterparty = counterpartyRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        Optional<DocumentEntity> document = documentRepository.findAny(DocumentSpecifications.counterparty(counterparty));
        if (document.isPresent()) ExceptionUtils.throwRelationDeleteException();
        counterpartyRepository.delete(counterparty);
    }
}
