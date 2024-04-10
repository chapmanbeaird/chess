package websocket;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/chess")
public class ChessWebSocketHandler {

    private final Gson gson = new Gson();
    private final GameDAO gameDao;
    private final AuthDAO authDao;
    private final ChessConnectionManager connectionManager = new ChessConnectionManager();

    public ChessWebSocketHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
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

    private void handleJoinPlayer(Session session, UserGameCommand command) throws DataAccessException {

        if (!(command instanceof UserGameCommand.JoinPlayerCommand)) {
            sendErrorMessage(session, "Invalid command data for joining as a player.");
            return;
        }

        UserGameCommand.JoinPlayerCommand joinCommand = (UserGameCommand.JoinPlayerCommand) command;
        String authtoken = joinCommand.getAuthString();
        String playerName = authDao.getUsername(authtoken);
        try {
            // Add player to the session manager
            connectionManager.add(joinCommand.getGameID(), session, playerName);

            // Send the updated game state to the player
            GameData updatedGame = gameDao.getGame(joinCommand.getGameID());
            if (updatedGame != null) {
                session.getRemote().sendString(gson.toJson(new ServerMessage.LoadGameMessage(updatedGame.game())));
                ServerMessage.NotificationMessage notificationMessage =
                        new ServerMessage.NotificationMessage(playerName + " joined as " + joinCommand.getPlayerColor());

                // Broadcast to all participants in the game, except the joining player
                connectionManager.broadcast(joinCommand.getGameID(), playerName, notificationMessage);
            } else {
                sendErrorMessage(session, "Game not found.");
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Error processing join game: " + e.getMessage());
        }
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
        try {
            ServerMessage.ErrorMessage error = new ServerMessage.ErrorMessage(errorMessage);
            String jsonError = gson.toJson(error);
            session.getRemote().sendString(jsonError);
        } catch (IOException e) {
            // Handle the case where the message could not be sent.
            e.printStackTrace();
        }
    }

}
