package ServiceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LogoutService;
import static org.junit.jupiter.api.Assertions.*;

class LogoutTest {

    private AuthDAO authDAO;
    private LogoutService logoutService;
    private String validAuthToken;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new AuthDAO();
        logoutService = new LogoutService(authDAO);

        // Setup a valid authToken for testing
        validAuthToken = "validAuthToken";
        authDAO.createAuthToken(new AuthData(validAuthToken, "testUser"));
    }

    @Test
    void testSuccessfulLogout() throws DataAccessException {
        // Perform logout with a valid authToken
        assertDoesNotThrow(() -> logoutService.logoutUser(validAuthToken), "Logout should succeed without throwing an exception");

        // Attempt to retrieve the deleted authToken to confirm logout
        assertNull(authDAO.getAuthToken(validAuthToken), "AuthToken should be null after logout");
    }

    @Test
    void testInvalidAuthToken() {
        // Attempt to logout with an invalid authToken
        assertThrows(DataAccessException.class, () -> logoutService.logoutUser("invalidAuthToken"), "Should throw DataAccessException for non-existing authToken");
    }
}
