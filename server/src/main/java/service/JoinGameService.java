package service;

import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameData;
import chess.ChessGame;

public class JoinGameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public JoinGameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void joinGame(int gameId, String playerColor, String authToken) throws DataAccessException {
        GameData game = gameDAO.getGame(gameId);
        String username = authDAO.getUsername(authToken); // Fetch AuthData using authToken


        if (game == null) {
            throw new DataAccessException("Game not found.");
        }

        // Check if the player is already in the game
        if (isPlayerAlreadyInGame(game, username)) {
            throw new DataAccessException("Player already in this game.");
        }

        // Logic to add player to the game based on playerColor
        GameData updatedGame;
        if ("WHITE".equals(playerColor)) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("White player spot already taken.");
            }
            updatedGame = new GameData(game.gameID(), authToken, game.blackUsername(), game.gameName(), game.game());
        } else if ("BLACK".equals(playerColor)) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Black player spot already taken.");
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), authToken, game.gameName(), game.game());
        } else {
            // Handle the case for joining as an observer
            return;
        }

        gameDAO.updateGame(gameId, updatedGame); // Update the game
    }

    private boolean isPlayerAlreadyInGame(GameData game, String username) {
        return username.equals(game.whiteUsername()) || username.equals(game.blackUsername());
    }
}