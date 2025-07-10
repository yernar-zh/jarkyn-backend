package kz.jarkyn.backend.export.core;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelRow {
    private final ExcelContext excelContext;
    private final Sheet sheet;
    private final Row row;
    private final ExcelCellStyle defaultCellStyle;
    private final int rowIndex;
    private int colIndex = 0;

    public ExcelRow(Workbook workbook, Sheet sheet, ExcelCellStyle defaultCellStyle, int rowIndex) {
        this.excelContext = new ExcelContext(workbook);
        this.sheet = sheet;
        this.row = sheet.createRow(rowIndex);
        this.rowIndex = rowIndex;
        this.defaultCellStyle = defaultCellStyle;
    }

    public ExcelRow setWidth(Integer width) {
        row.setHeight((short) (width * 256 / 21.2));
        return this;
    }


    public ExcelRow addCell(String text) {
        return addCell(text, defaultCellStyle, 1);
    }

    public ExcelRow addCell(String text, int joinRows) {
        return addCell(text, defaultCellStyle, joinRows);

    }

    public ExcelRow addCell(String text, ExcelCellStyle style) {
        return addCell(text, style, 1);
    }

    public ExcelRow addCell(String text, ExcelCellStyle style, int joinRows) {
        Cell cell = row.createCell(colIndex++);
        cell.setCellValue(text);
        addStyle(cell, style, joinRows);
        return this;
    }

    public ExcelRow addCell(Number amount) {
        return addCell(amount, defaultCellStyle, 1);
    }

    public ExcelRow addCell(Number amount, int joinRows) {
        return addCell(amount, defaultCellStyle, joinRows);
    }

    public ExcelRow addCell(Number amount, ExcelCellStyle style) {
        return addCell(amount, style, 1);
    }

    public ExcelRow addCell(Number amount, ExcelCellStyle style, int joinRows) {
        Cell cell = row.createCell(colIndex++);
        cell.setCellValue(amount.doubleValue());
        addStyle(cell, style, joinRows);
        return this;
    }

    private void addStyle(Cell cell, ExcelCellStyle style, int joinRows) {
        if (style != null) {
            cell.setCellStyle(excelContext.getCellStyle(style));
        } else {
            cell.setCellStyle(excelContext.getCellStyle(defaultCellStyle));
        }
        if (joinRows > 1) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, colIndex - 1, colIndex + joinRows - 2));
        }
        for (int i = 0; i < joinRows - 1; i++) {
            Cell mergedCell = row.createCell(colIndex++);
            if (style != null) {
                mergedCell.setCellStyle(excelContext.getCellStyle(style));
            } else {
                mergedCell.setCellStyle(excelContext.getCellStyle(defaultCellStyle));
            }
        }
    }
}
