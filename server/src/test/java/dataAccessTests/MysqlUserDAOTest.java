package dataAccessTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlUserDAOTest {
    private MysqlUserDAO userDAO;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new MysqlUserDAO();
        encoder = new BCryptPasswordEncoder();
        userDAO.clearUsers(); // Reset state before each test
    }

    @Test
    public void testCreateUserAndVerify() throws DataAccessException {
        // Setup
        String username = "testUser";
        String email = "test@example.com";
        String password = "testPassword";
        UserData userData = new UserData(username, password, email);

        // Operation
        userDAO.createUser(userData);

        // Assertion for createUser
        assertTrue(userDAO.isUsernameUsed(username));
        assertTrue(userDAO.isEmailUsed(email));

        // Verification for verifyUser
        assertTrue(userDAO.verifyUser(username, password));
    }

    @Test
    public void testVerifyUserWithWrongPassword() throws DataAccessException {
        // Setup
        String username = "testUser2";
        String email = "test2@example.com";
        String password = "rightPassword";
        UserData userData = new UserData(username, password, email);
        userDAO.createUser(userData);

        // Operation & Assertion
        assertFalse(userDAO.verifyUser(username, "wrongPassword"));
    }

    @Test
    public void testIsEmailUsed() throws DataAccessException {
        // Setup
        String email = "unique@example.com";
        String username = "uniqueUser";
        String password = "uniquePass";
        UserData userData = new UserData(username, password, email);

        // Operation
        userDAO.createUser(userData);

        // Assertion
        assertTrue(userDAO.isEmailUsed(email));
        assertFalse(userDAO.isEmailUsed("notused@example.com"));
    }

    @Test
    public void testIsUsernameUsed() throws DataAccessException {
        // Setup
        String username = "existingUser";
        UserData userData = new UserData(username,"pass123" ,"existing@example.com");

        // Operation
        userDAO.createUser(userData);

        // Assertion
        assertTrue(userDAO.isUsernameUsed(username));
        assertFalse(userDAO.isUsernameUsed("nonExistingUser"));
    }

    @Test
    public void testGetUser() throws DataAccessException {
        // Setup
        String username = "getUserTest";
        UserData userData = new UserData(username, "getPass", "getuser@example.com");
        userDAO.createUser(userData);

        // Operation
        UserData retrieved = userDAO.getUser(username);

        // Assertion
        assertNotNull(retrieved);
        assertEquals(username, retrieved.username());
        assertEquals("getuser@example.com", retrieved.email());
    }

    @Test
    public void testGetNonexistentUser() throws DataAccessException {
        // Operation & Assertion
        assertNull(userDAO.getUser("nonExistentUser"));
    }
}
