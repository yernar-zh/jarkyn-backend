package kz.jarkyn.backend.export.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.document.core.model.dto.ItemRequest;
import kz.jarkyn.backend.document.supply.model.dto.SupplyRequest;
import kz.jarkyn.backend.export.service.ExportCurrency;
import kz.jarkyn.backend.export.service.ExportService;
import kz.jarkyn.backend.global.service.CurrencyService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@RestController
@RequestMapping(Api.Export.PATH)
public class ExportController {
    private final ExportService exportService;
    private final CurrencyService currencyService;

    public ExportController(
            ExportService exportService,
            CurrencyService currencyService
    ) {
        this.exportService = exportService;
        this.currencyService = currencyService;
    }

    @PostMapping("receiptNote/xlsx")
    public ResponseEntity<Resource> receiptNoteXlsx(
            @RequestBody SupplyRequest request
    ) {
        String currencyCode = currencyService.findApiById(request.getCurrency().getId()).getCode();
        boolean fraction = existFraction(request.getAmount(),
                request.getItems().stream().map(ItemRequest::getPrice).toList());

        Resource resource = exportService.generateXlsx(
                ExportService.Template.RECEIPT_NOTE,
                Map.of("document", request),
                ExportCurrency.of(fraction, currencyCode));

        return ResponseEntity.ok().body(resource);
    }

    @PostMapping("receiptNote/pdf")
    public ResponseEntity<Resource> receiptNotePdf(
            @RequestBody SupplyRequest request
    ) {
        String currencyCode = currencyService.findApiById(request.getCurrency().getId()).getCode();
        boolean fraction = existFraction(request.getAmount(),
                request.getItems().stream().map(ItemRequest::getPrice).toList());

        Resource resource = exportService.generatePdf(
                ExportService.Template.RECEIPT_NOTE,
                Map.of("document", request),
                ExportCurrency.of(fraction, currencyCode));

        return ResponseEntity.ok().body(resource);
    }


    private boolean existFraction(BigDecimal bd, List<BigDecimal> bds) {
        return Stream.concat(bds.stream(), Stream.of(bd))
                .map(BigDecimal::stripTrailingZeros)
                .anyMatch(value -> value.scale() > 0);
    }
}