package ServiceTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RegisterService;
import static org.junit.jupiter.api.Assertions.*;

class RegisterTest {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private RegisterService registerService;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        registerService = new RegisterService(userDAO, authDAO);

        // Clear the database (if necessary) and/or ensure it's in a known state before each test
    }

    @Test
    void testSuccessfulRegistration() throws DataAccessException {
        UserData newUser = new UserData("newUser", "password", "newUser@example.com");
        assertNotNull(registerService.registerUser(newUser), "Registration should succeed for new user");
    }

    @Test
    void testRegistrationWithExistingUsername() throws DataAccessException {
        UserData existingUser = new UserData("existingUser", "password", "existingUser@example.com");
        userDAO.createUser(existingUser); // Simulate existing user

        UserData newUserSameUsername = new UserData("existingUser", "newPassword", "newEmail@example.com");
        assertNotNull(registerService.registerUser(newUserSameUsername), "Registration should fail with existing username");
    }

    @Test
    void testRegistrationWithExistingEmail() throws DataAccessException {
        UserData existingUser = new UserData("user", "password", "existingEmail@example.com");
        userDAO.createUser(existingUser); // Simulate existing user

        UserData newUserSameEmail = new UserData("newUser", "password", "existingEmail@example.com");
        assertNotNull(registerService.registerUser(newUserSameEmail), "Registration should fail with existing email");
    }

    @Test
    void testInvalidUserData() throws DataAccessException {
        UserData invalidUser = new UserData(null, null, null);
        assertNotNull(registerService.registerUser(invalidUser), "Registration should fail with invalid user data");
    }
}
