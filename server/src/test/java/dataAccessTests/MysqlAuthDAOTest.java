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
}
