package service;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;
import java.util.UUID;

public class RegisterService {
    private UserDAO userDAO;

    public RegisterService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static String generateAuthToken() {
        // Using UUID
        String token = UUID.randomUUID().toString();
        return token;
    }


    public boolean registerUser(UserData userData) throws DataAccessException {
        // Validate user data
        if (userData == null || userData.username() == null || userData.password() == null || userData.email() == null) {
            return false; // Invalid user data
        }

        // Check if the username or email is already taken
        if (userDAO.getUser(userData.username()) != null) {
            return false;
        }

        // Check if email is already used
         if (userDAO.getUser(userData.email()) != null) {
             return false; // Email already in use
         }

        // Store the user in the database
        userDAO.createUser(userData);
        return true;
    }

}
