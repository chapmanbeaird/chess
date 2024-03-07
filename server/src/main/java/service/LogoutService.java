package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;

public class LogoutService {
    private final AuthDAO authDAO;

    public LogoutService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void logoutUser(String authToken) throws DataAccessException {
        // Delete the authToken
        authDAO.deleteAuthToken(authToken);
    }
}
