package service;

import dataAccess.GameDAO;
import dataAccess.DataAccessException;
import model.GameData;
import java.util.Random;

public class CreateGameService {
    private GameDAO gameDAO;
    private static final Random random = new Random();

    public CreateGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public GameData createGame(String gameName) throws DataAccessException {
        if (gameDAO.gameNameExists(gameName)){
            throw new DataAccessException("Game name already taken");
        }

        int gameId = generateGameId();
        GameData newGame = new GameData(gameId, null, null, gameName, null);

        gameDAO.createGame(newGame);
        return newGame;
    }

    // Generates a random game ID
    private int generateGameId() {
        return random.nextInt(Integer.MAX_VALUE);
    }
}
