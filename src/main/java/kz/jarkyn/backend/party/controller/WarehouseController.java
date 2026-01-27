package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.MessageResponse;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.WarehouseRequest;
import kz.jarkyn.backend.party.model.dto.WarehouseResponse;
import kz.jarkyn.backend.party.service.WarehouseService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

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
        return warehouseService.findApiByFilter(QueryParams.ofMulty(allParams));
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

    @PutMapping("{id}/archive")
    public WarehouseResponse archive(@PathVariable UUID id) {
        return warehouseService.archive(id);
    }

    @PutMapping("{id}/unarchive")
    public WarehouseResponse unarchive(@PathVariable UUID id) {
        return warehouseService.unarchive(id);
    }

    @DeleteMapping("{id}")
    public MessageResponse delete(@PathVariable UUID id) {
        warehouseService.delete(id);
        return MessageResponse.DELETED;
    }
}