package kz.jarkyn.backend.document.supply.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.supply.model.dto.SupplyDetailResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyRequest;
import kz.jarkyn.backend.good.model.dto.GoodResponse;
import kz.jarkyn.backend.document.supply.service.SupplyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(Api.Supply.PATH)
public class SupplyController {
    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }


    @GetMapping("{id}")
    public SupplyDetailResponse detail(@PathVariable UUID id) {
        return supplyService.findApiById(id);
    }

    @PostMapping
    public SupplyDetailResponse create(@RequestBody SupplyRequest request) {
        UUID id = supplyService.createApi(request);
        return supplyService.findApiById(id);
    }

    @PutMapping("{id}")
    public SupplyDetailResponse edit(@PathVariable UUID id, @RequestBody SupplyRequest request) {
        supplyService.editApi(id, request);
        return supplyService.findApiById(id);
    }
}