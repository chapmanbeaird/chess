package dataAccess;

import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class MysqlUserDAO implements UserDAO{
    private final static String CREATE_USER = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
    private final static String GET_USER = "SELECT * FROM users WHERE username = ?";
    private final static String CHECK_EMAIL = "SELECT * FROM users WHERE email = ?";
    private final static String CHECK_USERNAME = "SELECT * FROM users WHERE username = ?";
    private final static String CLEAR_USERS = "DELETE FROM users";
    private final static String CHECK_IF_EMPTY = "SELECT COUNT(*) AS rowcount FROM users";

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();



    public void createUser(UserData user) throws DataAccessException {
        // Use SQL INSERT statement to add user
        String hashedPassword = encoder.encode(user.password()); //hash the password

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_USER)) {

            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting new user into the database", e);
        }
    }

    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        // Read the hashed password from the database
        String storedHashedPassword;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                storedHashedPassword = rs.getString("password");
            } else {
                // If the user doesn't exist, we can't verify the password
                return false;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving hashed password from the database", e);
        }

        return encoder.matches(providedClearTextPassword, storedHashedPassword);
    }

    public boolean isEmailUsed(String email) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_EMAIL)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean emailExists = rs.next();
                return emailExists;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while checking if email is used: " + email, e);
        }
    }


    public boolean isUsernameUsed(String username) throws DataAccessException {
        // Use SQL SELECT statement to check if username is used
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_USERNAME)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while checking username", e);
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        // Use SQL SELECT statement to get user
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String user = rs.getString("username");
                String password = rs.getString("password");
                String email = rs.getString("email");
                return new UserData(user, password, email);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving user", e);
        }
    }

    public void clearUsers() throws DataAccessException {
        // Use SQL DELETE statement to clear users
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CLEAR_USERS)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing users", e);
        }
    }

    public boolean isEmpty() throws DataAccessException {
        return DatabaseUtility.isEmpty();
    }
}
