package handler;

public class SimpleResponse {
    private final String message;

    public SimpleResponse(String message) {
        this.message = message;
    }

    // Getter for message to enable serialization by Gson
    public String getMessage() {
        return message;
    }
}
