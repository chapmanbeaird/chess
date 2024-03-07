package service;

import dataAccess.DataAccessException;
import dataAccess.mysqlAuthDAO;

public class LogoutService {
    private final mysqlAuthDAO authDAO;

    public LogoutService(mysqlAuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logoutUser(String authToken) throws DataAccessException {
        // Delete the authToken
        authDAO.deleteAuthToken(authToken);
    }
}
