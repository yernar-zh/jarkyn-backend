package kz.jarkyn.backend.warehouse.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseRequest;
import kz.jarkyn.backend.warehouse.model.dto.WarehouseResponse;
import kz.jarkyn.backend.warehouse.service.WarehouseService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
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
    public PageResponse<WarehouseResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return warehouseService.findApiByFilter(QueryParams.of(allParams));
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