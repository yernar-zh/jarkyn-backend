package kz.jarkyn.backend.excel.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.excel.service.SupplyExcelService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(Api.Excel.PATH)
public class ExcelController {

    private final SupplyExcelService supplyExcelService;

    public ExcelController(SupplyExcelService supplyExcelService) {
        this.supplyExcelService = supplyExcelService;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadExcel() throws IOException {

        Workbook workbook = supplyExcelService.getInvoice(UUID.fromString("17c1285b-6514-45d5-88a2-3b9f673dc5e3"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=example.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }
}