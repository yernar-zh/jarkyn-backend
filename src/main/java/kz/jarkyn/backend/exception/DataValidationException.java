package kz.jarkyn.backend.exception;

public class DataValidationException extends ValidationException {
    private final String code;

    public DataValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}