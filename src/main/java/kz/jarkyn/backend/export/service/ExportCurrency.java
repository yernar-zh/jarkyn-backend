package kz.jarkyn.backend.export.service;

public class ExportCurrency {
    private final boolean fractional;
    private final String code;

    private ExportCurrency(boolean fractional, String code) {
        this.fractional = fractional;
        this.code = code;
    }

    public static ExportCurrency of(boolean fractional, String code) {
        return new ExportCurrency(fractional, code);
    }

    public boolean isFractional() {
        return fractional;
    }

    public String getCode() {
        return code;
    }
}
