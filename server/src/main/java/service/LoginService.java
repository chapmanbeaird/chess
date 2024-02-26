package service;

import dataAccess.AuthDAO;
import dataAccess.UserDAO;
import dataAccess.DataAccessException;
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
        if (user == null || !user.password().equals(password)) {
            return null; // User not found or password does not match
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
