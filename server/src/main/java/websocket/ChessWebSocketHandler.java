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

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("New connection: " + session.getRemoteAddress().getAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
         //remove player from connection
//        ChessConnectionManager manager = new ChessConnectionManager();
//        manager.remove(session);
        System.out.println("Connection closed: " + session.getRemoteAddress().getAddress() + ", Reason: " + reason);
    }

    private void handleJoinPlayer(Session session, UserGameCommand command) throws DataAccessException {

        if (!(command instanceof JoinPlayerCommand)) {
            sendErrorMessage(session, "Invalid command data for joining as a player.");
            return;
        }

        JoinPlayerCommand joinCommand = (JoinPlayerCommand) command;
        String authtoken = joinCommand.getAuthString();
        String playerName = authDao.getUsername(authtoken);
        try {
            // Add player to the session manager
            connectionManager.add(joinCommand.getGameID(), session, playerName);

            // Send the updated game state to the player
            GameData updatedGame = gameDao.getGame(joinCommand.getGameID());
            if (updatedGame != null) {
                session.getRemote().sendString(gson.toJson(new LoadGameMessage(updatedGame.game())));
                NotificationMessage notificationMessage =
                        new NotificationMessage(playerName + " joined as " + joinCommand.getPlayerColor());

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
        if (!(command instanceof JoinObserverCommand)) {
            sendErrorMessage(session, "Invalid command data for joining as an observer.");
            return;
        }

        JoinObserverCommand joinCommand = (JoinObserverCommand) command;
        String authtoken = joinCommand.getAuthString();
        String observerName = authDao.getUsername(authtoken);

        try {
            // Add observer to the session manager
            connectionManager.add(joinCommand.getGameID(), session, observerName);

            // Broadcast to all participants in the game, including the new observer
            NotificationMessage notificationMessage =
                    new NotificationMessage(observerName + " is now observing the game.");
            connectionManager.broadcast(joinCommand.getGameID(), observerName, notificationMessage);

        } catch (Exception e) {
            sendErrorMessage(session, "Error processing join observer: " + e.getMessage());
        }
    }


    private void handleMakeMove(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof MakeMoveCommand)) {
            sendErrorMessage(session, "Invalid command data for making a move.");
            return;
        }

        MakeMoveCommand moveCommand = (MakeMoveCommand) command;
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
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            connectionManager.broadcast(moveCommand.getGameID(), playerName, loadGameMessage);

            NotificationMessage notificationMessage =
                    new NotificationMessage(moveMessage);
            connectionManager.broadcast(moveCommand.getGameID(), null, notificationMessage);

        } catch (Exception e) {
            sendErrorMessage(session, "Error processing make move: " + e.getMessage());
        }
    }



    private void handleLeave(Session session, UserGameCommand command) throws DataAccessException {
        if (!(command instanceof LeaveCommand)) {
            sendErrorMessage(session, "Invalid command data for leaving the game.");
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
            sendErrorMessage(session, "Invalid command data for resigning from the game.");
            return;
        }

        ResignCommand resignCommand = (ResignCommand) command;
        String authtoken = resignCommand.getAuthString();
        String playerName = authDao.getUsername(authtoken);

        try {
            // Handle the resignation in the game logic
            GameData gameData = gameDao.getGame(resignCommand.getGameID());
            if (gameData == null) {
                sendErrorMessage(session, "Game not found.");
                return;
            }

            // Determine the team color of the resigning player
            ChessGame.TeamColor resigningTeam = null;
            if (gameData.whiteUsername().equals(playerName)) {
                resigningTeam = ChessGame.TeamColor.WHITE;
            } else if (gameData.blackUsername().equals(playerName)) {
                resigningTeam = ChessGame.TeamColor.BLACK;
            }

            // Resign the game
            gameData.game().resign(resigningTeam);
            String winner = gameData.game().getWinner();

            // Persist the updated game state and winner
            gameDao.updateGame(resignCommand.getGameID(), gameData);

            // Notify all clients that the game has ended due to resignation
            String notification = playerName + " has resigned. " + winner + " wins the game.";
            NotificationMessage notificationMessage =
                    new NotificationMessage(notification);
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
