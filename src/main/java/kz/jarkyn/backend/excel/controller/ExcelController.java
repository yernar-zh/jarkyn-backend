package kz.jarkyn.backend.excel.controller;

import kz.jarkyn.backend.core.controller.Api;
import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyResponse;
import kz.jarkyn.backend.excel.core.ExcelCellStyle;
import kz.jarkyn.backend.excel.core.ExcelSheet;
import kz.jarkyn.backend.excel.core.ExcelUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping(Api.Export.PATH)
public class ExcelController {


    @PostMapping("/supply")
    public ResponseEntity<Resource> supply(@RequestBody SupplyResponse supply) {
        boolean fraction = Stream.concat(Stream.of(supply.getAmount()),
                        supply.getItems().stream().map(ItemResponse::getPrice))
                .anyMatch(bd -> bd.stripTrailingZeros().scale() > 0);
        String currencyCode = supply.getCurrency().getCode();

        Workbook workbook = new XSSFWorkbook();
        ExcelSheet sheet = new ExcelSheet(workbook, style());
        sheet.setWidth(List.of(45, 75, 350, 70, 125, 135));

        // Заголовок
        sheet.addRow();
        String headerText = "Инвойс № " + supply.getName() + " от " + ExcelUtils.dateFormat(supply.getMoment());
        sheet.addRow().addCell(headerText, style().bold().horCenter().fontHeight(16), 6);
        sheet.addRow();

        sheet.addRow().setWidth(30).addCell("Реквизиты:", 2).addCell(
                "", style().borderBottom(), 4);
        sheet.addRow().setWidth(30).addCell("Покупатель:", 2).addCell(
                supply.getCounterparty().getName(), style().borderBottom(), 4);
        sheet.addRow();

        sheet.addRow()
                .addCell("№", style().borderAll())
                .addCell("Наименование товара", style().borderAll(), 2)
                .addCell("Кол-во", style().borderAll())
                .addCell("Цена", style().borderAll())
                .addCell("Сумма", style().borderAll());
        for (int i = 0; i < supply.getItems().size(); i++) {
            ItemResponse item = supply.getItems().get(i);
            sheet.addRow()
                    .addCell(i + 1, style().borderAll())
                    .addCell(item.getGood().getName(), style().borderAll(), 2)
                    .addCell(item.getQuantity(), style().borderAll().numberFormat(fraction, null))
                    .addCell(item.getPrice(), style().borderAll().numberFormat(fraction, currencyCode))
                    .addCell(item.getPrice().multiply(new BigDecimal(item.getQuantity())),
                            style().borderAll().numberFormat(fraction, currencyCode));
        }
        sheet.addRow();
        sheet.addRow().addCell("").addCell("").addCell("").addCell("Итого:")
                .addCell(supply.getAmount(), style().bold().fontHeight(18).numberFormat(fraction, currencyCode), 2);


        return toResponse(workbook, "supply_" + supply.getName());
    }

    private ResponseEntity<Resource> toResponse(Workbook workbook, String fileName) {
        if (true) {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".xlsx")
                        .contentType(MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .contentLength(resource.contentLength())
                        .body(resource);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
           return null;
        }
    }

    private ExcelCellStyle style() {
        return new ExcelCellStyle();
    }
}