package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.UUID;

public class LoginService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public LoginService(UserDAO userDAO, AuthDAO authDAO, BCryptPasswordEncoder encoder) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.encoder = encoder;
    }

    public String loginUser(String username, String password) throws DataAccessException {
        // Check if user exists and password matches
        UserData user = userDAO.getUser(username);
        if (user == null) {
            // User does not exist, return null
            return null;
        }
        if (!encoder.matches(password, user.password())) {
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
