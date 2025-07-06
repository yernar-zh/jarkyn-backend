package kz.jarkyn.backend.excel.core;

import org.apache.poi.ss.usermodel.*;


import org.apache.poi.ss.usermodel.HorizontalAlignment;

public final class ExcelCellStyle {
    private HorizontalAlignment horizontalAlignment;
    private BorderStyle borderTop;
    private BorderStyle borderBottom;
    private BorderStyle borderLeft;
    private BorderStyle borderRight;
    private boolean wrapText;

    private boolean bold;
    private boolean italic;
    private int fontHeight;
    private String fontName;
    private boolean strikeout;

    private String dataFormat;

    public ExcelCellStyle() {
        this.horizontalAlignment = HorizontalAlignment.GENERAL;
        this.borderTop = BorderStyle.NONE;
        this.borderBottom = BorderStyle.NONE;
        this.borderLeft = BorderStyle.NONE;
        this.borderRight = BorderStyle.NONE;
        this.wrapText = false;
        this.bold = false;
        this.italic = false;
        this.fontHeight = 11;
        this.fontName = "Arial";
        this.strikeout = false;
        this.dataFormat = null;
    }

    public ExcelCellStyle horCenter() {
        this.horizontalAlignment = HorizontalAlignment.CENTER;
        return this;
    }

    public ExcelCellStyle borderBottom() {
        this.borderBottom = BorderStyle.THIN;
        return this;
    }

    public ExcelCellStyle borderAll() {
        this.borderTop = BorderStyle.THIN;
        this.borderLeft = BorderStyle.THIN;
        this.borderBottom = BorderStyle.THIN;
        this.borderRight = BorderStyle.THIN;
        return this;
    }

    public ExcelCellStyle bold() {
        this.bold = true;
        return this;
    }

    public ExcelCellStyle italic() {
        this.italic = true;
        return this;
    }

    public ExcelCellStyle fontHeight(int fontHeight) {
        this.fontHeight = fontHeight;
        return this;
    }

    public ExcelCellStyle numberFormat(boolean fraction, String currencyCode) {
        if (currencyCode == null) {
            this.dataFormat = "# ##0" + (fraction ? ".00" : "");
            return this;
        }
        String symbol = switch (currencyCode) {
            case "USD" -> " \"$\"";
            case "CNY" -> " \"¥\"";
            case "KZT" -> " \"₸\"";
            default -> throw new IllegalStateException("Unexpected value: " + currencyCode);
        };
        this.dataFormat = "# ##0" + (fraction ? ".00" : "") + symbol;
        return this;
    }


    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public BorderStyle getBorderTop() {
        return borderTop;
    }

    public BorderStyle getBorderBottom() {
        return borderBottom;
    }

    public BorderStyle getBorderLeft() {
        return borderLeft;
    }

    public BorderStyle getBorderRight() {
        return borderRight;
    }

    public boolean isWrapText() {
        return wrapText;
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public int getFontHeight() {
        return fontHeight;
    }

    public String getFontName() {
        return fontName;
    }

    public boolean isStrikeout() {
        return strikeout;
    }

    public String getDataFormat() {
        return dataFormat;
    }
}