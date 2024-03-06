package dataAccess;

import model.UserData;
import java.sql.*;

public class mysqlUserDAO {
    private final static String CREATE_USER = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
    private final static String GET_USER = "SELECT * FROM users WHERE username = ?";
    private final static String CHECK_EMAIL = "SELECT * FROM users WHERE email = ?";
    private final static String CHECK_USERNAME = "SELECT * FROM users WHERE username = ?";

    public void createUser(UserData user) throws DataAccessException {
        // Use SQL INSERT statement to add user
    }

    public boolean isEmailUsed(String email) {
        // Use SQL SELECT statement to check if email is used
    }

    public boolean isUsernameUsed(String username) {
        // Use SQL SELECT statement to check if username is used
    }

    public UserData getUser(String username){
        // Use SQL SELECT statement to get user
    }

    public void clearUsers() {
        // Use SQL DELETE statement to clear users
    }
}
