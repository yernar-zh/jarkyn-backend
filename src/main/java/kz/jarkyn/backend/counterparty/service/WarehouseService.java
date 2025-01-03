
package kz.jarkyn.backend.counterparty.service;


import kz.jarkyn.backend.audit.service.AuditService;
import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.counterparty.model.WarehouseEntity;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseRequest;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseResponse;
import kz.jarkyn.backend.counterparty.repository.WarehouseRepository;
import kz.jarkyn.backend.counterparty.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final AuditService auditService;

    public WarehouseService(
            WarehouseRepository warehouseRepository,
            WarehouseMapper warehouseMapper,
            AuditService auditService
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findApiById(UUID id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return warehouseMapper.toResponse(warehouse);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> findApiAll() {
        List<WarehouseEntity> warehouses = warehouseRepository.findAll();
        return warehouseMapper.toResponse(warehouses);
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
