package ServiceTests;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClearServiceTest {

    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearService clearService;

    @BeforeEach
    void setUp() throws DataAccessException{
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);

        // Add dummy data to DAOs
        userDAO.createUser(new UserData("user1", "pass1", "email1@example.com"));
        gameDAO.createGame(new GameData(1, "user1", "user2", "game1", new ChessGame()));
        authDAO.createAuthToken(new AuthData("token1", "user1"));
    }

    @Test
    void testClear() throws DataAccessException {
        clearService.clear();

        // Assertions to check if each DAO is cleared
        assertTrue(userDAO.isEmpty(), "UserDAO should be empty after clear");
        assertTrue(gameDAO.isEmpty(), "GameDAO should be empty after clear");
        assertTrue(authDAO.isEmpty(), "AuthDAO should be empty after clear");
    }
}
