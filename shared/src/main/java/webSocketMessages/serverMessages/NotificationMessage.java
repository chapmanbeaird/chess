package webSocketMessages.serverMessages;

/**
 * Notification message sent by the server to inform players of events.
 */
public  class NotificationMessage extends ServerMessage {
    private String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
