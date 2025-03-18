package kz.jarkyn.backend.party.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.party.model.dto.SupplierListResponse;
import kz.jarkyn.backend.party.model.dto.SupplierRequest;
import kz.jarkyn.backend.party.model.dto.SupplierResponse;
import kz.jarkyn.backend.party.service.SupplierService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Supplier.PATH)
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(
            SupplierService supplierService
    ) {
        this.supplierService = supplierService;
    }

    @GetMapping("{id}")
    public SupplierResponse detail(@PathVariable UUID id) {
        return supplierService.findApiById(id);
    }

    @GetMapping
    public PageResponse<SupplierListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return supplierService.findApiByFilter(QueryParams.of(allParams));
    }

    @PostMapping
    public SupplierResponse create(@RequestBody SupplierRequest request) {
        UUID id = supplierService.createApi(request);
        return supplierService.findApiById(id);
    }

    @PutMapping("{id}")
    public SupplierResponse edit(@PathVariable UUID id, @RequestBody SupplierRequest request) {
        supplierService.editApi(id, request);
        return supplierService.findApiById(id);
    }
}