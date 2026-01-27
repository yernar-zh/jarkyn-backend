
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
import kz.jarkyn.backend.party.model.WarehouseEntity;
import kz.jarkyn.backend.good.model.WarehouseEntity_;
import kz.jarkyn.backend.party.model.dto.WarehouseResponse;
import kz.jarkyn.backend.party.model.dto.WarehouseRequest;
import kz.jarkyn.backend.party.repository.WarehouseRepository;
import kz.jarkyn.backend.party.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AuditService auditService;
    private final DocumentRepository documentRepository;
    private final SearchFactory searchFactory;

    public WarehouseService(
            WarehouseRepository warehouseRepository,
            WarehouseMapper warehouseMapper,
            AuditService auditService,
            DocumentRepository documentRepository,
            SearchFactory searchFactory
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.auditService = auditService;
        this.documentRepository = documentRepository;
        this.searchFactory = searchFactory;
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findApiById(UUID id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return warehouseMapper.toResponse(warehouse);
    }

    @Transactional(readOnly = true)
    public PageResponse<WarehouseResponse> findApiByFilter(QueryParams queryParams) {
        CriteriaAttributes<WarehouseEntity> attributes = CriteriaAttributes.<WarehouseEntity>builder()
                .add("id", (root) -> root.get(WarehouseEntity_.id))
                .add("name", (root) -> root.get(WarehouseEntity_.name))
                .add("archived", (root) -> root.get(WarehouseEntity_.archived))
                .build();
        Search<WarehouseResponse> search = searchFactory.createCriteriaSearch(
                WarehouseResponse.class, QueryParams.NAME_SEARCH, QueryParams.Sort.NAME_ASC,
                WarehouseEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(WarehouseRequest request) {
        WarehouseEntity warehouse = warehouseRepository.save(warehouseMapper.toEntity(request));
        auditService.saveEntity(warehouse);
        return warehouse.getId();
    }

    @Transactional
    public void editApi(UUID id, WarehouseRequest request) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        warehouseMapper.editEntity(warehouse, request);
        auditService.saveEntity(warehouse);
    }

    @Transactional
    public WarehouseResponse archive(UUID id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        warehouse.setArchived(true);
        auditService.archive(warehouse);
        return findApiById(id);
    }

    @Transactional
    public WarehouseResponse unarchive(UUID id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        warehouse.setArchived(false);
        auditService.unarchive(warehouse);
        return findApiById(id);
    }

    @Transactional
    public void delete(UUID id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        Optional<DocumentEntity> document = documentRepository.findAny(DocumentSpecifications.warehouse(warehouse));
        if (document.isPresent()) ExceptionUtils.throwRelationDeleteException();
        warehouseRepository.delete(warehouse);
    }
}
