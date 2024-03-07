package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;

import java.util.List;

public class ListGamesService {
    private GameDAO gameDAO;

    public ListGamesService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public List<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }
}
