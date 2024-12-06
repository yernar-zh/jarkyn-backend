package kz.jarkyn.backend.document.sale.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseRequest;
import kz.jarkyn.backend.counterparty.model.dto.WarehouseResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleDetailResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.good.model.dto.GoodListResponse;
import kz.jarkyn.backend.good.model.dto.GoodRequest;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.document.sale.service.SaleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public SaleDetailResponse detail(@PathVariable UUID id) {
        return saleService.findApiById(id);
    }

    @GetMapping
    public List<GoodResponse> list(@RequestParam Map<String, String> allParams) {
        return saleService.findApiByFilter(QueryParams.of(allParams));
    }

    @PostMapping
    public SaleDetailResponse create(@RequestBody SaleRequest request) {
        UUID id = saleService.createApi(request);
        return saleService.findApiById(id);
    }

    @PutMapping("{id}")
    public SaleDetailResponse edit(@PathVariable UUID id, @RequestBody SaleRequest request) {
        saleService.editApi(id, request);
        return saleService.findApiById(id);
    }
}