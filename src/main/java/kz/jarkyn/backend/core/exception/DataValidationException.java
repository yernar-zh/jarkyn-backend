package kz.jarkyn.backend.core.exception;

public class DataValidationException extends ValidationException {
    private final String code;

    public DataValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public DataValidationException(String message) {
        super(message);
        this.code = "USER_EXCEPTION";
    }


    public String getCode() {
        return code;
    }
}