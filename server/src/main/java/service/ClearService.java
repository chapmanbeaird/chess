package service;

import dataAccess.UserDAO;
import dataAccess.GameDAO;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;

public class ClearService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public ClearService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
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


