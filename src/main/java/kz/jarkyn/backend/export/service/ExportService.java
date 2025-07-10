
package kz.jarkyn.backend.export.service;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.Map;
import java.util.Objects;

@Service
public class ExportService {
    @Value("classpath:jxls/template/receiptNote.xlsx")
    private Resource receiptNoteResource;

    private final ExportUtils exportUtils;
    private final WebClient webClient;

    public ExportService(ExportUtils exportUtils, WebClient gotenbergWebClient) {
        this.exportUtils = exportUtils;
        this.webClient = gotenbergWebClient;
    }

    public Resource generateXlsx(Template template, Map<String, Object> args, ExportCurrency currency) {
        ByteArrayOutputStream temp;
        try (InputStream is = getResource(template).getInputStream();
             ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            Workbook workbook = new XSSFWorkbook(is);
            if (currency != null) {
                String symbol = switch (currency.getCode()) {
                    case "KZT" -> "₸";
                    case "USD" -> "$";
                    case "CNY" -> "¥";
                    default -> throw new IllegalStateException("Unexpected value: " + currency);
                };
                String dataFormatStr = "# ##0" + (currency.isFractional() ? ".00" : "") + " \"" + symbol + "\"";
                short dataFormat = workbook.createDataFormat().getFormat(dataFormatStr);
                workbook.forEach(sheet -> sheet.forEach(row -> row.forEach(cell -> {
                    Comment comment = cell.getCellComment();
                    if (comment == null || !comment.getString().toString().equals("app:currency")) return;
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.cloneStyleFrom(cell.getCellStyle());
                    cellStyle.setDataFormat(dataFormat);
                    cell.setCellStyle(cellStyle);
                    cell.removeCellComment();
                })));
            }
            workbook.write(os);
            temp = os;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream temp2;
        try (InputStream is = new ByteArrayInputStream(temp.toByteArray());
             ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            Context context = new Context();
            args.forEach(context::putVar);
            context.putVar("utils", exportUtils);
            JxlsHelper.getInstance().processTemplate(is, os, context);
            temp2 = os;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream is = new ByteArrayInputStream(temp2.toByteArray());
             ByteArrayOutputStream os = new ByteArrayOutputStream()
        ) {
            Workbook workbook = new XSSFWorkbook(is);
            workbook.forEach(sheet -> sheet.forEach(row -> {
                float maxHeight = row.getHeightInPoints();  // Start with existing height
                for (Cell cell : row) {
                    CellStyle style = cell.getCellStyle();
                    if (style.getWrapText() && cell.getCellType() == CellType.STRING) {
                        String text = cell.getStringCellValue();
                        Font font = workbook.getFontAt(style.getFontIndex());
                        int fontSize = font.getFontHeightInPoints(); // Approx height per line
                        int lineCount = estimateLineCount(text, sheet.getColumnWidth(cell.getColumnIndex()), fontSize);
                        float estimatedHeight = lineCount * fontSize + 4; // Add padding
                        if (estimatedHeight > maxHeight) {
                            maxHeight = estimatedHeight;
                        }
                    }
                }
                row.setHeightInPoints(maxHeight);
            }));
            workbook.write(os);
            return new ByteArrayResource(os.toByteArray()) {
                @Override
                public String getFilename() {
                    return "result.xlsx";
                }
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource generatePdf(Template template, Map<String, Object> args, ExportCurrency currency) {
        Resource xlsxResource = generateXlsx(template, args, currency);
        byte[] response = webClient.post()
                .uri("/forms/libreoffice/convert")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("files", xlsxResource))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("Gotenberg Error Response: " + errorBody);
                                    return Mono.error(new RuntimeException("Gotenberg error: " + errorBody));
                                })
                )
                .bodyToMono(byte[].class)
                .block();
        return new ByteArrayResource(Objects.requireNonNull(response)) {
            @Override
            public String getFilename() {
                return "result.pdf";
            }
        };
    }

    private static int estimateLineCount(String text, int columnWidthUnits, int fontSize) {
        int avgCharPerLine = (int) ((columnWidthUnits - 2) * 0.1934 / fontSize);
        String[] lines = text.split("\n"); // If multiline already
        int lineCount = 0;
        for (String line : lines) {
            int lineLength = line.length();
            lineCount += (int) Math.ceil((double) lineLength / avgCharPerLine);
        }
        return Math.max(lineCount, 1);
    }

    private Resource getResource(Template template) {
        return switch (template) {
            case RECEIPT_NOTE -> receiptNoteResource;
        };
    }

    public enum Template {
        RECEIPT_NOTE
    }
}
