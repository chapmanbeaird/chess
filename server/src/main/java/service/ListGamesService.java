package service;

import dataAccess.DataAccessException;
import dataAccess.mysqlGameDAO;
import model.GameData;

import java.util.List;

public class ListGamesService {
    private mysqlGameDAO gameDAO;

    public ListGamesService(mysqlGameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public List<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }
}
