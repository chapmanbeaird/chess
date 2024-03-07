package service;

import dataAccess.DataAccessException;
import dataAccess.mysqlAuthDAO;
import dataAccess.mysqlGameDAO;
import dataAccess.mysqlUserDAO;

public class ClearService {

    private final mysqlUserDAO userDAO;
    private final mysqlGameDAO gameDAO;
    private final mysqlAuthDAO authDAO;

    public ClearService(mysqlUserDAO userDAO, mysqlGameDAO gameDAO, mysqlAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        // Clear all data in each DAO
        userDAO.clearUsers();
        gameDAO.clearGames();
        authDAO.clearAuthTokens();
    }
}


