package kz.jarkyn.backend.model.common.api;

public class MessageApi {
    public static final MessageApi DELETED = MessageApi.of("DELETED");

    private final String message;

    private MessageApi(String message) {
        this.message = message;
    }

    public static MessageApi of(String message) {
        return new MessageApi(message);
    }

    public String getMessage() {
        return message;
    }
}
