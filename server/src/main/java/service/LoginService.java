package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
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
