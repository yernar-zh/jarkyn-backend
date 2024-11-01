
package kz.jarkyn.backend.warehouse.service;


import kz.jarkyn.backend.core.exception.ExceptionUtils;
import kz.jarkyn.backend.warehouse.mode.WarehouseEntity;
import kz.jarkyn.backend.warehouse.mode.dto.WarehouseRequest;
import kz.jarkyn.backend.warehouse.mode.dto.WarehouseResponse;
import kz.jarkyn.backend.warehouse.repository.WarehouseRepository;
import kz.jarkyn.backend.warehouse.service.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseService(
            WarehouseRepository warehouseRepository,
            WarehouseMapper warehouseMapper
    ) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findApiById(UUID id) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        return warehouseMapper.toApi(warehouse);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> findApiAll() {
        return warehouseRepository.findAll().stream().map(warehouseMapper::toApi).toList();
    }

    @Transactional
    public UUID createApi(WarehouseRequest request) {
        WarehouseEntity warehouse = warehouseRepository.save(warehouseMapper.toEntity(request));
        return warehouse.getId();
    }

    @Transactional
    public void editApi(UUID id, WarehouseRequest request) {
        WarehouseEntity warehouse = warehouseRepository.findById(id).orElseThrow(ExceptionUtils.entityNotFound());
        warehouseMapper.editEntity(warehouse, request);
    }
}
