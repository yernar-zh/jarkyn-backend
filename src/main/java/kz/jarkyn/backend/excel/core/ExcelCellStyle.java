package kz.jarkyn.backend.excel.core;

import org.apache.poi.ss.usermodel.*;


import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public final class ExcelCellStyle {
    private final HorizontalAlignment horizontalAlignment;
    private final VerticalAlignment verticalAlignment;
    private final BorderStyle borderTop;
    private final BorderStyle borderBottom;
    private final BorderStyle borderLeft;
    private final BorderStyle borderRight;
    private final boolean wrapText;

    private final boolean bold;
    private final boolean italic;
    private final int fontHeight;
    private final String fontName;
    private final boolean strikeout;

    private final String dataFormat;

    public ExcelCellStyle() {
        this(
                HorizontalAlignment.GENERAL,
                VerticalAlignment.BOTTOM,
                BorderStyle.NONE,
                BorderStyle.NONE,
                BorderStyle.NONE,
                BorderStyle.NONE,
                false,
                false,
                false,
                11,
                "Arial",
                false,
                null
        );
    }

    public ExcelCellStyle(
            HorizontalAlignment horizontalAlignment,
            VerticalAlignment verticalAlignment,
            BorderStyle borderTop,
            BorderStyle borderBottom,
            BorderStyle borderLeft,
            BorderStyle borderRight,
            boolean wrapText,
            boolean bold,
            boolean italic,
            int fontHeight,
            String fontName,
            boolean strikeout,
            String dataFormat
    ) {
        this.horizontalAlignment = horizontalAlignment;
        this.verticalAlignment = verticalAlignment;
        this.borderTop = borderTop;
        this.borderBottom = borderBottom;
        this.borderLeft = borderLeft;
        this.borderRight = borderRight;
        this.wrapText = wrapText;
        this.bold = bold;
        this.italic = italic;
        this.fontHeight = fontHeight;
        this.fontName = fontName;
        this.strikeout = strikeout;
        this.dataFormat = dataFormat;
    }

    public ExcelCellStyle withHorizontalAlignment(HorizontalAlignment alignment) {
        return new ExcelCellStyle(alignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withVerticalAlignment(VerticalAlignment alignment) {
        return new ExcelCellStyle(horizontalAlignment, alignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withBorderTop(BorderStyle style) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, style, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withBorderBottom(BorderStyle style) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, style, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withBorderLeft(BorderStyle style) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, style, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withBorderRight(BorderStyle style) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, style,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withBorderAll(BorderStyle style) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, style, style, style, style,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }


    public ExcelCellStyle withWrapText(boolean wrapText) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withBold(boolean bold) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withItalic(boolean italic) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withFontHeight(int fontHeight) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withFontName(String fontName) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withStrikeout(boolean strikeout) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }

    public ExcelCellStyle withDataFormat(String dataFormat) {
        return new ExcelCellStyle(horizontalAlignment, verticalAlignment, borderTop, borderBottom, borderLeft, borderRight,
                wrapText, bold, italic, fontHeight, fontName, strikeout, dataFormat);
    }


    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
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