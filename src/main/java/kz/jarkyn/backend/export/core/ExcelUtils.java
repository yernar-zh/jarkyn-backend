package kz.jarkyn.backend.export.core;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ExcelUtils {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.of("Asia/Almaty"));

    public static String dateFormat(Instant instant) {
        return DATE_FORMAT.format(instant);
    }

    public static String numberFormat(String currencyCode, boolean fraction) {
        if (currencyCode == null) return "# ##0" + (fraction ? ".00" : "");
        String symbol = switch (currencyCode) {
            case "USD" -> " \"$\"";
            case "CNY" -> " \"¥\"";
            case "KZT" -> " \"₸\"";
            default -> throw new IllegalStateException("Unexpected value: " + currencyCode);
        };
        return "# ##0" + (fraction ? ".00" : "") + symbol;
    }
}
