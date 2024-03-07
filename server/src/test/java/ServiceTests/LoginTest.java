package ServiceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoginService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private LoginService loginService;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        encoder = new BCryptPasswordEncoder();
        authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO, encoder);
        // Create a new hashed password
        String hashedPassword = encoder.encode("testPass");
        // Add a dummy user
        userDAO.createUser(new UserData("testUser", hashedPassword, "testEmail@example.com"));
    }

    @Test
    void testValidLogin() throws DataAccessException {
        // Attempt to login with valid credentials
        String authToken = loginService.loginUser("testUser", "testPass");

        // Assert authToken is not null (login successful)
        assertNotNull(authToken, "Auth token should not be null for valid login");
    }

    @Test
    void testInvalidUsername() throws DataAccessException {
        // Attempt to login with an invalid username
        String authToken = loginService.loginUser("invalidUser", "testPass");
        assertNull(authToken, "Logging in with an invalid username should return null");
    }

    @Test
    void testInvalidPassword() throws DataAccessException {
        // Attempt to login with a valid username but invalid password
        String authToken = loginService.loginUser("testUser", "wrongPass");
        assertNull(authToken, "Logging in with an invalid password should return null");
    }
}
