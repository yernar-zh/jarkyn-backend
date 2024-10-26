package kz.jarkyn.backend.controller;

import kz.jarkyn.backend.config.Api;
import kz.jarkyn.backend.model.document.sale.api.SaleDetailResponse;
import kz.jarkyn.backend.model.document.sale.api.SaleRequest;
import kz.jarkyn.backend.model.good.api.GoodRequest;
import kz.jarkyn.backend.model.good.api.GoodResponse;
import kz.jarkyn.backend.model.good.apiFilter.GoodApiFilter;
import kz.jarkyn.backend.service.SaleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Api.Good.PATH)
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
    public List<GoodResponse> list(@ModelAttribute GoodApiFilter filter) {
        return null;
    }

    @PostMapping
    public SaleDetailResponse create(@RequestBody SaleRequest request) {
        UUID id = saleService.createApi(request);
        return saleService.findApiById(id);
    }

    @PutMapping("{id}")
    public GoodResponse edit(@PathVariable UUID id, @RequestBody GoodRequest request) {
        return null;
    }
}