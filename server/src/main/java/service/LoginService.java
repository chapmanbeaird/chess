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

    public String loginUser(String username, String password, String email) throws DataAccessException {
        // Check if user exists and password matches
        UserData user = userDAO.getUser(username);

        if (user == null) {
            // User does not exist, create a new user
            user = new UserData(username, password, email);
            userDAO.createUser(user);
        } else if (!user.password().equals(password)) {
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
