package kz.jarkyn.backend.document.supply.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.supply.model.dto.SupplyRequest;
import kz.jarkyn.backend.document.supply.model.dto.SupplyListResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyResponse;
import kz.jarkyn.backend.export.service.ExportService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import kz.jarkyn.backend.document.supply.service.SupplyService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Supply.PATH)
public class SupplyController {
    private final SupplyService supplyService;
    private final DocumentService documentService;

    public SupplyController(
            SupplyService supplyService,
            DocumentService documentService) {
        this.supplyService = supplyService;
        this.documentService = documentService;
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

    @DeleteMapping("{id}")
    public SupplyResponse delete(@PathVariable UUID id) {
        supplyService.delete(id);
        return supplyService.findApiById(id);
    }

    @PostMapping("{id}/restore")
    public SupplyResponse restore(@PathVariable UUID id) {
        supplyService.restore(id);
        return supplyService.findApiById(id);
    }

    @PostMapping("export/deliveryNote/xlsx")
    public ResponseEntity<Resource> exportDeliveryNoteXlsx(@RequestBody SupplyRequest request) {
        Resource resource = documentService.generateXlsx(
                ExportService.Template.SUPPLY_RECEIPT_NOTE,
                request, request.getItems());
        return ResponseEntity.ok().body(resource);
    }

    @PostMapping("export/deliveryNote/pdf")
    public ResponseEntity<Resource> exportDeliveryNotePdf(@RequestBody SupplyRequest request) {
        Resource resource = documentService.generatePdf(
                ExportService.Template.SUPPLY_RECEIPT_NOTE,
                request, request.getItems());
        return ResponseEntity.ok().body(resource);
    }
}
