package kz.jarkyn.backend.excel.core;

import org.apache.poi.ss.usermodel.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExcelContext {
    private final Map<List<Object>, Font> fonts = new HashMap<>();
    private final Map<String, Short> dataFormats = new HashMap<>();
    private final Map<List<Object>, CellStyle> cellStyles = new HashMap<>();
    private final Workbook workbook;

    public ExcelContext(Workbook workbook) {
        this.workbook = workbook;
    }

    public CellStyle getCellStyle(ExcelCellStyle excelCellStyle) {
        Font font = getFont(excelCellStyle);
        short dataFormat = getDataFormat(excelCellStyle);
        List<Object> key = List.of(excelCellStyle.getHorizontalAlignment(), excelCellStyle.getVerticalAlignment(),
                excelCellStyle.getBorderTop(), excelCellStyle.getBorderBottom(), excelCellStyle.getBorderLeft(),
                excelCellStyle.getBorderRight(), excelCellStyle.isWrapText(), font, dataFormat);
        return cellStyles.computeIfAbsent(key, _ -> {
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(excelCellStyle.getHorizontalAlignment());
            cellStyle.setVerticalAlignment(excelCellStyle.getVerticalAlignment());
            cellStyle.setBorderTop(excelCellStyle.getBorderTop());
            cellStyle.setBorderBottom(excelCellStyle.getBorderBottom());
            cellStyle.setBorderLeft(excelCellStyle.getBorderLeft());
            cellStyle.setBorderRight(excelCellStyle.getBorderRight());
            cellStyle.setWrapText(excelCellStyle.isWrapText());
            cellStyle.setFont(font);
            if (dataFormat != Short.MIN_VALUE) cellStyle.setDataFormat(dataFormat);
            return cellStyle;
        });
    }

    private Font getFont(ExcelCellStyle excelCellStyle) {
        List<Object> key = List.of(excelCellStyle.isBold(), excelCellStyle.isItalic(), excelCellStyle.getFontHeight(),
                excelCellStyle.getFontName(), excelCellStyle.isStrikeout());
        return fonts.computeIfAbsent(key, _ -> {
            Font font = workbook.createFont();
            font.setBold(excelCellStyle.isBold());
            font.setItalic(excelCellStyle.isItalic());
            font.setFontHeightInPoints((short) excelCellStyle.getFontHeight());
            font.setFontName(excelCellStyle.getFontName());
            font.setStrikeout(excelCellStyle.isStrikeout());
            return font;
        });
    }

    private short getDataFormat(ExcelCellStyle excelCellStyle) {
        if (excelCellStyle.getDataFormat() == null) return Short.MIN_VALUE;
        return dataFormats.computeIfAbsent(excelCellStyle.getDataFormat(), _ -> {
            DataFormat dataFormat = workbook.createDataFormat();
            return dataFormat.getFormat(excelCellStyle.getDataFormat());
        });
    }
}
