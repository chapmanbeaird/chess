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

    @Test
    public void testCreateUserDuplicateUsernameOrEmail() throws DataAccessException {
        String username = "duplicateUser";
        String email = "duplicate@example.com";
        String password = "testPassword";
        UserData firstUser = new UserData(username, password, email);
        UserData secondUser = new UserData(username, "anotherPassword", "duplicate@example.com");

        userDAO.createUser(firstUser);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(secondUser), "Expected DataAccessException for duplicate username or email.");
    }
    @Test
    public void testClearUsers() throws DataAccessException {
        // Setup: Assuming a user has been added
        userDAO.createUser(new UserData("clearUser", "password", "clear@example.com"));

        // Operation
        userDAO.clearUsers();

        // Assertion: Check if table is empty
        assertTrue(userDAO.isEmpty(), "User table should be empty after calling clearUsers.");
    }
    @Test
    public void testIsEmptyTrue() throws DataAccessException {
        // Setup: Clear users table first to ensure it's empty
        userDAO.clearUsers();

        // Operation & Assertion: Check if table is empty
        assertTrue(userDAO.isEmpty(), "User table should be empty.");
    }

    @Test
    public void testIsEmptyFalse() throws DataAccessException {
        // Setup: Add a user to ensure the table is not empty
        userDAO.createUser(new UserData("nonEmptyUser", "password", "nonempty@example.com"));

        // Operation & Assertion: Check if table is not empty
        assertFalse(userDAO.isEmpty(), "User table should not be empty after adding a user.");
    }

}
