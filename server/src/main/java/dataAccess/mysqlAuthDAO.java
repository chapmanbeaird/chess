package dataAccess;

import java.sql.*;
import model.AuthData;

public class mysqlAuthDAO {

    private final static String CREATE_AUTH = "INSERT INTO auth_tokens (authToken, username) VALUES (?, ?)";
    private final static String GET_AUTH = "SELECT * FROM auth_tokens WHERE authToken = ?";
    private final static String DELETE_AUTH = "DELETE FROM auth_tokens WHERE authToken = ?";
    private final static String CLEAR_ALL_AUTH = "DELETE FROM auth_tokens";


    // Method to create a new Auth Token
    public void createAuthToken(AuthData authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_AUTH)) {

            stmt.setString(1, authToken.authToken());
            stmt.setString(2, authToken.username());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting new auth token into the database", e);
        }
    }

    // Method to retrieve an Auth Token by token
    public AuthData getAuthToken(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_AUTH)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String authToken = rs.getString("authToken");
                String username = rs.getString("username");
                return new AuthData(authToken, username);
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving auth token", e);
        }
    }

    // Method to get username by token
    public String getUsername(String token) throws DataAccessException {
        AuthData authData = getAuthToken(token);
        return (authData != null) ? authData.username() : null;
    }

    // Method to delete an Auth Token by token
    public void deleteAuthToken(String token) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_AUTH)) {

            stmt.setString(1, token);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Auth token deletion failed: it does not exist", null);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while deleting auth token", e);
        }
    }

    // Method to clear all Auth Tokens
    public void clearAuthTokens() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CLEAR_ALL_AUTH)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing auth toekns", e);
        }
    }
}
