package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.warehouse.api.WarehouseRequest;
import kz.jarkyn.backend.model.warehouse.api.WarehouseResponse;
import kz.jarkyn.backend.service.WarehouseService;
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