package service;

import dataAccess.DataAccessException;
import dataAccess.mysqlAuthDAO;
import dataAccess.mysqlUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class LoginService {
    private final mysqlUserDAO userDAO;
    private final mysqlAuthDAO authDAO;

    public LoginService(mysqlUserDAO userDAO, mysqlAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String loginUser(String username, String password) throws DataAccessException {
        // Check if user exists and password matches
        UserData user = userDAO.getUser(username);
        if (user == null) {
            // User does not exist, return null
            return null;
        }
        else if (!user.password().equals(password)) {
            // User exists, but password does not match
            return null;
        }

        // Generate a new authToken
        String authToken = generateAuthToken();

        // Create and store the new AuthData
        AuthData authData = new AuthData(authToken, username);
        authDAO.createAuthToken(authData);

        return authToken;
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
