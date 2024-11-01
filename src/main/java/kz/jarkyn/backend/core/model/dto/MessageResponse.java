package kz.jarkyn.backend.core.model.dto;

public class MessageResponse {
    public static final MessageResponse DELETED = MessageResponse.of("DELETED");

    private final String message;

    private MessageResponse(String message) {
        this.message = message;
    }

    public static MessageResponse of(String message) {
        return new MessageResponse(message);
    }

    public String getMessage() {
        return message;
    }
}
