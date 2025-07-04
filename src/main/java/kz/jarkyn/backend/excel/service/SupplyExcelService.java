package kz.jarkyn.backend.excel.service;

import kz.jarkyn.backend.document.core.model.dto.ItemResponse;
import kz.jarkyn.backend.document.supply.model.dto.SupplyResponse;
import kz.jarkyn.backend.document.supply.service.SupplyService;
import kz.jarkyn.backend.excel.core.ExcelCellStyle;
import kz.jarkyn.backend.excel.core.ExcelSheet;
import kz.jarkyn.backend.excel.core.ExcelUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class SupplyExcelService {
    private final SupplyService supplyService;

    public SupplyExcelService(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @Transactional(readOnly = true)
    public Workbook getInvoice(UUID supplyId) {
        SupplyResponse supply = supplyService.findApiById(supplyId);
        boolean fraction = Stream.concat(
                        Stream.of(supply.getAmount()), supply.getItems().stream().map(ItemResponse::getPrice))
                .anyMatch(bd -> bd.stripTrailingZeros().scale() > 0);

        Workbook workbook = new XSSFWorkbook();
        ExcelCellStyle defaultCellStyle = new ExcelCellStyle().withFontHeight(11).withBold(false);
        ExcelCellStyle headerCellStyle = defaultCellStyle.withBold(true).withFontHeight(16)
                .withHorizontalAlignment(HorizontalAlignment.CENTER);
        ExcelCellStyle underlineCellStyle = defaultCellStyle.withBorderBottom(BorderStyle.THIN);
        ExcelCellStyle borderCellStyle = defaultCellStyle.withBorderAll(BorderStyle.THIN);
        ExcelCellStyle borderNumberCellStyle = defaultCellStyle.withBorderAll(BorderStyle.THIN)
                .withDataFormat(ExcelUtils.numberFormat(null, fraction));
        ExcelCellStyle borderCurrencyCellStyle = defaultCellStyle.withBorderAll(BorderStyle.THIN)
                .withDataFormat(ExcelUtils.numberFormat(supply.getCurrency().getCode(), fraction));
        ExcelCellStyle bigCurrencyCellStyle = defaultCellStyle.withFontHeight(18)
                .withDataFormat(ExcelUtils.numberFormat(supply.getCurrency().getCode(), fraction));
        ExcelSheet sheet = new ExcelSheet(workbook, defaultCellStyle);
        sheet.setWidth(List.of(45, 75, 350, 70, 125, 135));

        // Заголовок
        sheet.addRow();
        String headerText = "Инвойс № " + supply.getName() + " от " + ExcelUtils.dateFormat(supply.getMoment());
        sheet.addRow().addCell(headerText, headerCellStyle, 6);
        sheet.addRow();

        sheet.addRow().setWidth(30).addCell("Реквизиты:", 2).addCell("", underlineCellStyle, 4);
        sheet.addRow().setWidth(30).addCell("Покупатель:", defaultCellStyle, 2).addCell(
                supply.getCounterparty().getName(), underlineCellStyle, 4);
        sheet.addRow();

        sheet.addRow()
                .addCell("№", borderCellStyle)
                .addCell("Наименование товара", borderCellStyle, 2)
                .addCell("Кол-во", borderNumberCellStyle)
                .addCell("Цена", borderCellStyle)
                .addCell("Сумма", borderCellStyle);
        for (int i = 0; i < supply.getItems().size(); i++) {
            ItemResponse item = supply.getItems().get(i);
            sheet.addRow()
                    .addCell(i + 1, borderCellStyle)
                    .addCell(item.getGood().getName(), borderCellStyle, 2)
                    .addCell(item.getQuantity(), borderCellStyle)
                    .addCell(item.getPrice(), borderCurrencyCellStyle)
                    .addCell(item.getPrice().multiply(new BigDecimal(item.getQuantity())), borderCurrencyCellStyle);
        }
        sheet.addRow();
        sheet.addRow().addCell("").addCell("").addCell("").addCell("Итого:")
                .addCell(supply.getAmount(), bigCurrencyCellStyle, 2);

        return workbook;
    }
}
