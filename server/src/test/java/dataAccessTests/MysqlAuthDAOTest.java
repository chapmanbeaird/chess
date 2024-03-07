package dataAccessTests;

import dataAccess.*;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlAuthDAOTest {
    private MysqlAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MysqlAuthDAO();
        authDAO.clearAuthTokens();
    }

    @Test
    public void testCreateAuthToken() throws DataAccessException {
        // Setup
        String authToken = "authToken123";
        String username = "User123";

        // Operation
        authDAO.createAuthToken(new AuthData(authToken, username));

        // Assertion
        AuthData retrieved = authDAO.getAuthToken(authToken);
        assertNotNull(retrieved);
        assertEquals(username, retrieved.username());
    }

    @Test
    public void testGetAuthTokenNotFound() throws DataAccessException {
        // Setup
        String authToken = "nonExistentToken";

        // Operation & Assertion
        assertNull(authDAO.getAuthToken(authToken));
    }

    @Test
    public void testDeleteAuthToken() throws DataAccessException {
        // Setup
        String authToken = "authTokenToDelete";
        String username = "UserToDelete";
        authDAO.createAuthToken(new AuthData(authToken, username));

        // Operation
        authDAO.deleteAuthToken(authToken);

        // Assertion
        assertNull(authDAO.getAuthToken(authToken));
    }

    @Test
    public void testDeleteNonExistentAuthToken() {
        String authToken = "nonExistentToken";

        assertThrows(DataAccessException.class, () -> authDAO.deleteAuthToken(authToken), "Expected DataAccessException for deleting a non-existent token.");
    }

    @Test
    public void testClearAuthTokens() throws DataAccessException {
        // Setup
        authDAO.createAuthToken(new AuthData("authToken123", "User123"));

        // Operation
        authDAO.clearAuthTokens();

        // Assertion
        assertTrue(authDAO.isEmpty());
    }
    @Test
    public void testCreateAuthTokenAlreadyExists() throws DataAccessException {
        // Setup
        String authToken = "authToken123";
        String username = "User123";
        authDAO.createAuthToken(new AuthData(authToken, username));

        // Operation & Assertion
        assertThrows(DataAccessException.class, () -> authDAO.createAuthToken(new AuthData(authToken, username)), "Expected DataAccessException when creating a token that already exists.");
    }

    @Test
    public void testIsEmptyFalse() throws DataAccessException {
        // Setup
        authDAO.createAuthToken(new AuthData("authToken123", "User123"));

        // Operation & Assertion
        assertFalse(authDAO.isEmpty());
    }

    @Test
    public void testIsEmptyTrue() throws DataAccessException {
        // Assuming the table is empty at this point

        // Operation & Assertion
        assertTrue(authDAO.isEmpty());
    }

}
