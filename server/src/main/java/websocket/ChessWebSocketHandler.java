package websocket;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;

@WebSocket
public class ChessWebSocketHandler {

    private final Gson gson = new Gson();
    private final GameDAO gameDao;
    private final AuthDAO authDao;
    private final ChessConnectionManager connectionManager = new ChessConnectionManager();


    public ChessWebSocketHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
            String commandType = jsonObject.get("commandType").getAsString();
            UserGameCommand command = null;

            switch (commandType) {
                case "JOIN_PLAYER":
                    command = gson.fromJson(message, JoinPlayerCommand.class);
                    handleJoinPlayer(session, command);
                    break;
                case "JOIN_OBSERVER":
                    command = gson.fromJson(message, JoinObserverCommand.class);
                    handleJoinObserver(session, command);
                    break;
                case "MAKE_MOVE":
                    command = gson.fromJson(message, MakeMoveCommand.class);
                    handleMakeMove(session, command);
                    break;
                case "LEAVE":
                    command = gson.fromJson(message, LeaveCommand.class);
                    handleLeave(session, command);
                    break;
                case "RESIGN":
                    command = gson.fromJson(message, ResignCommand.class);
                    handleResign(session, command);
                    break;
                default:
                    sendErrorMessage(session, "Error: Unrecognized command type.");
                    break;
            }
        } catch (Exception e) {
            sendErrorMessage(session, "An error occurred processing your request: " + e.getMessage());
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
         //remove player from connection
        System.out.println("Connection closed: " + session.getRemoteAddress().getAddress() + ", Reason: " + reason);
    }

    private void handleJoinPlayer(Session session, UserGameCommand command) throws DataAccessException {

        if (!(command instanceof JoinPlayerCommand)) {
            sendErrorMessage(session, "Error: Invalid command data for joining as a player.");
            return;
        }

        JoinPlayerCommand joinCommand = (JoinPlayerCommand) command;
        String authtoken = joinCommand.getAuthString();
        String playerName = authDao.getUsername(authtoken);
        GameData gameData = gameDao.getGame(joinCommand.getGameID());
        AuthData authData = authDao.getAuthToken(authtoken);


        try {
           // Check if the spot is correct
            if (joinCommand.getPlayerColor() == ChessGame.TeamColor.WHITE) {
                if (!gameData.whiteUsername().equals(playerName)) {
                    sendErrorMessage(session, "Error: White spot is already taken.");
                    return;
                }
            } else if (joinCommand.getPlayerColor() == ChessGame.TeamColor.BLACK) {
                if (!gameData.blackUsername().equals(playerName)) {
                    sendErrorMessage(session, "Error: Black spot is already taken.");
                    return;
                }
            }

            // Add player to the session manager
            connectionManager.add(joinCommand.getGameID(), session, playerName);
            // Send the updated game state to the player
            GameData updatedGame = gameDao.getGame(joinCommand.getGameID());
            if (updatedGame != null) {
                ChessGame game = updatedGame.game();
                if (game != null && game.getBoard() != null) {
                    System.out.println("Game and board are correctly loaded. Game ID: " + joinCommand.getGameID());
                } else {
                    System.err.println("Game or board is null. Game ID: " + joinCommand.getGameID());
                }
                String json = gson.toJson(new LoadGameMessage(game));
//                System.out.println("Serialized game state to send: " + json);
                session.getRemote().sendString(json);
//                session.getRemote().sendString(gson.toJson(new LoadGameMessage(updatedGame.game())));
                NotificationMessage notificationMessage =
                        new NotificationMessage(playerName + " joined as " + joinCommand.getPlayerColor());

                // Broadcast to all participants in the game, except the joining player
                connectionManager.broadcast(joinCommand.getGameID(), playerName, notificationMessage);
                System.out.println("Notification sent: " + notificationMessage.getMessage());
            } else {
                sendErrorMessage(session, "Error: Game not found.");
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Error processing join game: " + e.getMessage());
        }
    }
    private void handleJoinObserver(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof JoinObserverCommand)) {
            sendErrorMessage(session, "Error: Invalid command data for joining as an observer.");
            return;
        }

        JoinObserverCommand joinCommand = (JoinObserverCommand) command;
        String authtoken = joinCommand.getAuthString();
        String observerName = authDao.getUsername(authtoken);
        GameData gameData = gameDao.getGame(joinCommand.getGameID());
        if (gameData == null){
            sendErrorMessage(session, "Error: Game not found. Invalid Game ID");
            return;
        }
        if (observerName == null){
            sendErrorMessage(session, "Error: user not found. Bad authtoken");
            return;
        }
        try {
            // Add observer to the session manager
            connectionManager.add(joinCommand.getGameID(), session, observerName);
            // Send the updated game state to the player
            GameData updatedGame = gameDao.getGame(joinCommand.getGameID());
            if (updatedGame != null) {
                session.getRemote().sendString(gson.toJson(new LoadGameMessage(updatedGame.game())));
                // Broadcast to all participants in the game, including the new observer
                NotificationMessage notificationMessage =
                        new NotificationMessage(observerName + " is now observing the game.");
                connectionManager.broadcast(joinCommand.getGameID(), observerName, notificationMessage);
            }
        } catch (Exception e) {
            sendErrorMessage(session, "Error processing join observer: " + e.getMessage());
        }
    }


    private void handleMakeMove(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof MakeMoveCommand)) {
            sendErrorMessage(session, "Error: Invalid command data for making a move.");
            return;
        }

        MakeMoveCommand moveCommand = (MakeMoveCommand) command;
        String authtoken = moveCommand.getAuthString();
        String playerName;
        try {
            playerName = authDao.getUsername(authtoken);
        } catch (Exception e) {
            sendErrorMessage(session, "Error: Failed to authenticate user.");
            return;
        }

        GameData gameData;
        try {
            // Retrieve the game data
            gameData = gameDao.getGame(moveCommand.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Error: Game not found.");
                return;
            }

            // Check if the game is already over
            if (gameData.game().isGameOver()) {
                sendErrorMessage(session, "Error: This game is already over. No further moves can be made.");
                return;
            }

            // Get the current game state and the player's move
            ChessGame game = gameData.game();
            ChessMove move = moveCommand.getMove();
            ChessBoard board = game.getBoard();
            ChessPiece piece = board.getPiece(move.getStartPosition());
            if (piece == null) {
                sendErrorMessage(session, "Error: No piece at start position.");
                return;
            }

            // Determine if the player is trying to move their own piece
            boolean isPlayerWhite = playerName.equals(gameData.whiteUsername());
            boolean isPieceWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
            if (isPlayerWhite != isPieceWhite) { // If the player and piece colors don't match
                sendErrorMessage(session, "Error: You cannot move your opponent's pieces.");
                return;
            }

            // Attempt to make the move
            try {
                game.makeMove(move);
            } catch (InvalidMoveException e) {
                sendErrorMessage(session, "Error: Invalid move: " + e.getMessage());
                return;
            } catch (Exception e) {
                sendErrorMessage(session, "Error processing make move: " + e.getMessage());
                return;
            }

            // After the move, check for checkmate or stalemate
            boolean checkmate = game.isInCheckmate(game.getTeamTurn());
            boolean stalemate = game.isInStalemate(game.getTeamTurn());

            // Send the updated game state to all players and observers
            GameData updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDao.updateGame(moveCommand.getGameID(), updatedGameData);

            // Prepare the notification message
            String moveMessage = playerName + " moved " + piece +
                    " from " + move.getStartPosition() + " to " + move.getEndPosition();
            if (checkmate) {
                moveMessage += ". Checkmate!";
            } else if (stalemate) {
                moveMessage += ". Stalemate!";
            }

            // Broadcast the move and game state to all participants
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connectionManager.broadcast(moveCommand.getGameID(), null, loadGameMessage);

            NotificationMessage notificationMessage =
                    new NotificationMessage(moveMessage);
            connectionManager.broadcast(moveCommand.getGameID(), playerName, notificationMessage);

        } catch (Exception e) {
            sendErrorMessage(session, "Error processing make move: " + e.getMessage());
        }
    }



    private void handleLeave(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof LeaveCommand)) {
            sendErrorMessage(session, "Error: Invalid command data for leaving the game.");
            return;
        }

        LeaveCommand leaveCommand = (LeaveCommand) command;
        String authtoken = leaveCommand.getAuthString();
        String playerName = authDao.getUsername(authtoken);

        try {
            // Remove the player or observer from the session manager
            connectionManager.remove(leaveCommand.getGameID(), playerName);

            // Broadcast to all participants in the game that the player has left
            NotificationMessage notificationMessage =
                    new NotificationMessage(playerName + " has left the game.");
            connectionManager.broadcast(leaveCommand.getGameID(), playerName, notificationMessage);

        } catch (Exception e) {
            sendErrorMessage(session, "Error processing leave: " + e.getMessage());
        }
    }


    private void handleResign(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof ResignCommand)) {
            sendErrorMessage(session, "Error: Invalid command data for resigning from the game.");
            return;
        }

        ResignCommand resignCommand = (ResignCommand) command;
        String authtoken = resignCommand.getAuthString();
        String playerName;
        try {
            playerName = authDao.getUsername(authtoken);
        } catch (Exception e) {
            sendErrorMessage(session, "Error: Failed to authenticate user.");
            return;
        }

        try {
            // Handle the resignation in the game logic
            GameData gameData = gameDao.getGame(resignCommand.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Error: Game not found.");
                return;
            }
            // Check if the player trying to resign is actually a player in the game
            if (!playerName.equals(gameData.whiteUsername()) && !playerName.equals(gameData.blackUsername())) {
                // The client trying to resign is not a player (thus, an observer or unauthenticated user)
                sendErrorMessage(session, "Error: Only players participating in the game can resign.");
                return;
            }
            // Mark the game as over due to resignation and determine the winner
            ChessGame game = gameData.game();
            ChessGame.TeamColor resigningTeam = gameData.whiteUsername().equals(playerName) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            game.resign(resigningTeam); // This marks the game as over and sets the winner
            String winner = game.getWinner();

            // Update the game in the database
            gameDao.updateGame(resignCommand.getGameID(), gameData);

            // Notify all clients (players and observers) about the resignation
            String notification = playerName + " has resigned. " + winner + " wins the game.";
            NotificationMessage notificationMessage = new NotificationMessage(notification);
            connectionManager.broadcast(resignCommand.getGameID(), null, notificationMessage);

        } catch (Exception e) {
            sendErrorMessage(session, "Error processing resignation: " + e.getMessage());
        }
    }


    private void sendErrorMessage(Session session, String errorMessage) {
        try {
            ErrorMessage error = new ErrorMessage(errorMessage);
            String jsonError = gson.toJson(error);
            session.getRemote().sendString(jsonError);
        } catch (IOException e) {
            // Handle the case where the message could not be sent.
            e.printStackTrace();
        }
    }

}
