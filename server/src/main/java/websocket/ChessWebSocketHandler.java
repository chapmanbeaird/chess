package websocket;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chess")
public class ChessWebSocketHandler {

    private final Gson gson = new Gson();

    public ChessWebSocketHandler() {
        // Initialization logic here, if necessary
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case JOIN_PLAYER:
                    handleJoinPlayer(session, command);
                    break;
                case JOIN_OBSERVER:
                    handleJoinObserver(session, command);
                    break;
                case MAKE_MOVE:
                    handleMakeMove(session, command);
                    break;
                case LEAVE:
                    handleLeave(session, command);
                    break;
                case RESIGN:
                    handleResign(session, command);
                    break;
                default:
                    sendErrorMessage(session, "Unrecognized command type.");
                    break;
            }
        } catch (Exception e) {
            sendErrorMessage(session, "An error occurred processing your request: " + e.getMessage());
        }
    }

    private void handleJoinPlayer(Session session, UserGameCommand command) {
        // Logic to handle player joining a game
    }

    private void handleJoinObserver(Session session, UserGameCommand command) {
        // Logic to handle observer joining a game
    }

    private void handleMakeMove(Session session, UserGameCommand command) {
        // Logic to handle making a move in the game
    }

    private void handleLeave(Session session, UserGameCommand command) {
        // Logic to handle a player or observer leaving the game
    }

    private void handleResign(Session session, UserGameCommand command) {
        // Logic to handle a player resigning from the game
    }

    private void sendErrorMessage(Session session, String errorMessage) {
        ServerMessage.ErrorMessage error = new ServerMessage.ErrorMessage(errorMessage);
        String jsonError = gson.toJson(error);
        session.getAsyncRemote().sendText(jsonError);
    }

    // Additional utility methods for sending messages, handling game logic, etc., would go here
}
