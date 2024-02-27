package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.UUID;

public class RegisterService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public static String generateAuthToken() {
        // Using UUID
        String token = UUID.randomUUID().toString();
        return token;
    }


    public String registerUser(UserData userData) throws DataAccessException {
        // Validate user data
        if (userData == null || userData.username() == null || userData.password() == null || userData.email() == null) {
            return null;
        }

        // Check if the username is already taken
        if (userDAO.isUsernameUsed(userData.username())) {
            return null;
        }

        // Check if email is already used
         if (userDAO.isEmailUsed(userData.email())) {
             return null;
         }

        // Store the user and authToken in the database
        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.createAuthToken(authData);
        userDAO.createUser(userData);
        return authToken;
    }

}
