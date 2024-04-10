package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
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
    private void handleJoinObserver(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof UserGameCommand.JoinObserverCommand)) {
            sendErrorMessage(session, "Invalid command data for joining as an observer.");
            return;
        }

        UserGameCommand.JoinObserverCommand joinCommand = (UserGameCommand.JoinObserverCommand) command;
        String authtoken = joinCommand.getAuthString();
        String observerName = authDao.getUsername(authtoken);

        try {
            // Add observer to the session manager
            connectionManager.add(joinCommand.getGameID(), session, observerName);

            // Broadcast to all participants in the game, including the new observer
            ServerMessage.NotificationMessage notificationMessage =
                    new ServerMessage.NotificationMessage(observerName + " is now observing the game.");
            connectionManager.broadcast(joinCommand.getGameID(), observerName, notificationMessage);

        } catch (Exception e) {
            sendErrorMessage(session, "Error processing join observer: " + e.getMessage());
        }
    }


    private void handleMakeMove(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof UserGameCommand.MakeMoveCommand)) {
            sendErrorMessage(session, "Invalid command data for making a move.");
            return;
        }

        UserGameCommand.MakeMoveCommand moveCommand = (UserGameCommand.MakeMoveCommand) command;
        String authtoken = moveCommand.getAuthString();
        String playerName = authDao.getUsername(authtoken);

        try {
            // Retrieve the game data
            GameData gameData = gameDao.getGame(moveCommand.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Game not found.");
                return;
            }

            // Get the current game state and the player's move
            ChessGame game = gameData.game();
            ChessMove move = moveCommand.getMove();

            // Check if it's the correct player's turn
            if (!game.getTeamTurn().toString().equals(playerName)) {
                sendErrorMessage(session, "It's not your turn.");
                return;
            }

            // Attempt to make the move
            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                sendErrorMessage(session, "Invalid move: " + e.getMessage());
                return;
            }

            // After the move, check for checkmate or stalemate
            boolean checkmate = game.isInCheckmate(game.getTeamTurn());
            boolean stalemate = game.isInStalemate(game.getTeamTurn());

            // Send the updated game state to all players and observers
            GameData updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDao.updateGame(moveCommand.getGameID(), updatedGameData);

            // Prepare the notification message
            ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());
            if (piece == null) {
                sendErrorMessage(session, "No piece at start position.");
                return;
            }
            String moveMessage = playerName + " moved " + piece +
                    " from " + move.getStartPosition() + " to " + move.getEndPosition();
            if (checkmate) {
                moveMessage += ". Checkmate!";
            } else if (stalemate) {
                moveMessage += ". Stalemate!";
            }

            // Broadcast the move and game state to all participants
            ServerMessage.LoadGameMessage loadGameMessage = new ServerMessage.LoadGameMessage(gameData.game());
            connectionManager.broadcast(moveCommand.getGameID(), playerName, loadGameMessage);

            ServerMessage.NotificationMessage notificationMessage =
                    new ServerMessage.NotificationMessage(moveMessage);
            connectionManager.broadcast(moveCommand.getGameID(), null, notificationMessage);

        } catch (Exception e) {
            sendErrorMessage(session, "Error processing make move: " + e.getMessage());
        }
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
