package kz.jarkyn.backend.document.sale.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.document.sale.model.dto.SaleListResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleResponse;
import kz.jarkyn.backend.document.sale.service.SaleService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Sale.PATH)
public class SaleController {
    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }


    @GetMapping("{id}")
    public SaleResponse detail(@PathVariable UUID id) {
        return saleService.findApiById(id);
    }


    @GetMapping
    public PageResponse<SaleListResponse> list(@RequestParam Map<String, String> allParams) {
        return saleService.findApiByFilter(QueryParams.of(allParams));
    }


    @PostMapping
    public SaleResponse create(@RequestBody SaleRequest request) {
        UUID id = saleService.createApi(request);
        return saleService.findApiById(id);
    }

    @PutMapping("{id}")
    public SaleResponse edit(@PathVariable UUID id, @RequestBody SaleRequest request) {
        saleService.editApi(id, request);
        return saleService.findApiById(id);
    }

    @PutMapping("{id}/commit")
    public SaleResponse commit(@PathVariable UUID id) {
        saleService.commit(id);
        return saleService.findApiById(id);
    }

    @PutMapping("{id}/undoCommit")
    public SaleResponse undoCommit(@PathVariable UUID id) {
        saleService.undoCommit(id);
        return saleService.findApiById(id);
    }
}