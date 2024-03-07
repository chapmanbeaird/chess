package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

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

        if (username == null){
            throw new IllegalArgumentException("No user found with provided authtoken");
        }

        // Logic to add player to the game based on playerColor
        GameData updatedGame;
        if ("WHITE".equals(playerColor)) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("White player spot already taken.");
            }
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if ("BLACK".equals(playerColor)) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Black player spot already taken.");
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            // Handle the case for joining as an observer
            return;
        }

        gameDAO.updateGame(gameId, updatedGame); // Update the game
    }
}
