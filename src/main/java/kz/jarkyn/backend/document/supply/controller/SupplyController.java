package kz.jarkyn.backend.document.supply.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.supply.model.dto.SupplyListResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyRequest;
import kz.jarkyn.backend.document.supply.service.SupplyService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Supply.PATH)
public class SupplyController {
    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @GetMapping("{id}")
    public SupplyResponse detail(@PathVariable UUID id) {
        return supplyService.findApiById(id);
    }


    @GetMapping
    public PageResponse<SupplyListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return supplyService.findApiByFilter(QueryParams.ofMulty(allParams));
    }


    @PostMapping
    public SupplyResponse create(@RequestBody SupplyRequest request) {
        UUID id = supplyService.createApi(request);
        return supplyService.findApiById(id);
    }

    @PutMapping("{id}")
    public SupplyResponse edit(@PathVariable UUID id, @RequestBody SupplyRequest request) {
        supplyService.editApi(id, request);
        return supplyService.findApiById(id);
    }

    @PutMapping("{id}/commit")
    public SupplyResponse commit(@PathVariable UUID id) {
        supplyService.commit(id);
        return supplyService.findApiById(id);
    }

    @PutMapping("{id}/undoCommit")
    public SupplyResponse undoCommit(@PathVariable UUID id) {
        supplyService.undoCommit(id);
        return supplyService.findApiById(id);
    }

    @DeleteMapping("{id}")
    public SupplyResponse delete(@PathVariable UUID id) {
        supplyService.delete(id);
        return supplyService.findApiById(id);
    }
}