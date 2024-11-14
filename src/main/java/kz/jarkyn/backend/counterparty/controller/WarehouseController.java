package kz.jarkyn.backend.counterparty.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseRequest;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseResponse;
import kz.jarkyn.backend.counterparty.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Warehouse.PATH)
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(
            WarehouseService warehouseService
    ) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("{id}")
    public WarehouseResponse detail(@PathVariable UUID id) {
        return warehouseService.findApiById(id);
    }

    @GetMapping
    public List<WarehouseResponse> list() {
        return warehouseService.findApiAll();
    }

    @PostMapping
    public WarehouseResponse create(@RequestBody WarehouseRequest request) {
        UUID id = warehouseService.createApi(request);
        return warehouseService.findApiById(id);
    }

    @PutMapping("{id}")
    public WarehouseResponse edit(@PathVariable UUID id, @RequestBody WarehouseRequest request) {
        warehouseService.editApi(id, request);
        return warehouseService.findApiById(id);
    }
}