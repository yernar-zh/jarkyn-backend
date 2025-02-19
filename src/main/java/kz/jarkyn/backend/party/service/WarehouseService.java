
package kz.jarkyn.backend.party.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.core.search.Search;
import kz.jarkyn.backend.core.search.SearchFactory;
import kz.jarkyn.backend.party.model.WarehouseEntity;
import kz.jarkyn.backend.party.model.dto.WarehouseRequest;
import kz.jarkyn.backend.party.model.dto.WarehouseResponse;
import kz.jarkyn.backend.party.repository.WarehouseRepository;
import kz.jarkyn.backend.party.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Search<WarehouseResponse> search = searchFactory.createListSearch(
                WarehouseResponse.class, List.of(), () -> warehouseMapper.toResponse(warehouseRepository.findAll()));
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
