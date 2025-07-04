package kz.jarkyn.backend.excel.core;

import org.apache.poi.ss.usermodel.*;

import java.util.List;

public class ExcelSheet {
    private final Workbook workbook;
    private final Sheet sheet;
    private final ExcelCellStyle defaultCellStyle;
    private int rowIndex = 0;

    public ExcelSheet(Workbook workbook, ExcelCellStyle defaultCellStyle) {
        this.workbook = workbook;
        this.sheet = workbook.createSheet("Sheet1");
        this.defaultCellStyle = defaultCellStyle;
    }

    public void setWidth(List<Integer> columnWidths) {
        for (int i = 0; i < columnWidths.size(); i++) {
            sheet.setColumnWidth(i, columnWidths.get(i) * 256 / 9);
        }
    }

    public ExcelRow addRow() {
        return new ExcelRow(workbook, sheet, defaultCellStyle, rowIndex++);
    }
}
