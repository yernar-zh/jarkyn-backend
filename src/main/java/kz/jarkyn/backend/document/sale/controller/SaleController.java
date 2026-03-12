package kz.jarkyn.backend.document.sale.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.core.model.dto.PageResponse;
import kz.jarkyn.backend.core.model.filter.QueryParams;
import kz.jarkyn.backend.document.core.service.DocumentService;
import kz.jarkyn.backend.document.sale.model.dto.SaleListResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleResponse;
import kz.jarkyn.backend.document.sale.model.dto.SaleRequest;
import kz.jarkyn.backend.document.sale.service.SaleService;
import kz.jarkyn.backend.export.service.ExportService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Api.Sale.PATH)
public class SaleController {
    private final SaleService saleService;
    private final DocumentService documentService;

    public SaleController(
            SaleService saleService,
            DocumentService documentService) {
        this.saleService = saleService;
        this.documentService = documentService;
    }

    @GetMapping("{id}")
    public SaleResponse detail(@PathVariable UUID id) {
        return saleService.findApiById(id);
    }


    @GetMapping
    public PageResponse<SaleListResponse> list(@RequestParam MultiValueMap<String, String> allParams) {
        return saleService.findApiByFilter(QueryParams.ofMulty(allParams));
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

    @DeleteMapping("{id}")
    public SaleResponse delete(@PathVariable UUID id) {
        saleService.delete(id);
        return saleService.findApiById(id);
    }

    @PostMapping("{id}/restore")
    public SaleResponse restore(@PathVariable UUID id) {
        saleService.restore(id);
        return saleService.findApiById(id);
    }

    @PostMapping("export/invoice/xlsx")
    public ResponseEntity<Resource> exportInvoiceXlsx(@RequestBody SaleRequest request) {
        Resource resource = documentService.generateXlsx(
                ExportService.Template.INVOICE,
                request,
                request.getItems());
        return ResponseEntity.ok().body(resource);
    }

    @PostMapping("export/invoice/pdf")
    public ResponseEntity<Resource> exportInvoicePdf(@RequestBody SaleRequest request) {
        Resource resource = documentService.generatePdf(
                ExportService.Template.INVOICE,
                request,
                request.getItems());
        return ResponseEntity.ok().body(resource);
    }
}
