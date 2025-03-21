
package kz.jarkyn.backend.warehouse.service;


import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.CriteriaAttributes;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.document.core.model.DocumentEntity_;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity;
import kz.jarkyn.backend.document.payment.model.PaidDocumentEntity_;
import kz.jarkyn.backend.document.supply.model.SupplyEntity;
import kz.jarkyn.backend.document.supply.model.SupplyEntity_;
import kz.jarkyn.backend.document.supply.model.dto.SupplyListResponse;
import kz.jarkyn.backend.global.model.CurrencyEntity_;
import kz.jarkyn.backend.party.model.OrganizationEntity_;
import kz.jarkyn.backend.party.model.PartyEntity_;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity;
import kz.jarkyn.backend.warehouse.model.WarehouseEntity_;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseRequest;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import kz.jarkyn.backend.warehouse.repository.WarehouseRepository;
import kz.jarkyn.backend.warehouse.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AuditService auditService;
    private final SearchFactory searchFactory;

    public WarehouseService(
            WarehouseRepository warehouseRepository,
            WarehouseMapper warehouseMapper,
            AuditService auditService,
            SearchFactory searchFactory
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.auditService = auditService;
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
                WarehouseResponse.class, List.of("name"),
                WarehouseEntity.class, attributes);
        return search.getResult(queryParams);
    }

    @Transactional
    public UUID createApi(WarehouseRequest request) {
        WarehouseEntity warehouse = warehouseRepository.save(warehouseMapper.toEntity(request));
        auditService.saveChanges(warehouse);
        return warehouse.getId();
    }

    @Transactional
    public void editApi(UUID id, WarehouseRequest request) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        warehouseMapper.editEntity(warehouse, request);
        auditService.saveChanges(warehouse);
    }
}
