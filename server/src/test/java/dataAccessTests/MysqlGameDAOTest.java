package dataAccessTests;

import dataAccess.*;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MysqlGameDAOTest {
    private MysqlGameDAO gameDAO;
    private ChessGame mockChessGame;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MysqlGameDAO();
        mockChessGame = new ChessGame();
        gameDAO.clearGames(); // Reset state before each test
    }

    @Test
    public void testCreateAndGetGame() throws DataAccessException {
        // Setup
        int gameId = 1;
        GameData gameData = new GameData(gameId, "whiteUser", "blackUser", "TestGame", mockChessGame);

        // Operation
        gameDAO.createGame(gameData);

        // Assertion
        GameData retrieved = gameDAO.getGame(gameId);
        assertNotNull(retrieved);
        assertEquals(gameId, retrieved.gameID());
        assertEquals("TestGame", retrieved.gameName());
        assertNotNull(retrieved.game());
    }

    @Test
    public void testGetGameNotFound() throws DataAccessException {
        // Setup
        int gameId = 999;

        // Operation & Assertion
        assertNull(gameDAO.getGame(gameId));
    }

    @Test
    public void testListGames() throws DataAccessException {

        int gameId1 = 1;
        gameDAO.createGame(new GameData(gameId1, "whiteUser1", "blackUser1", "Game1", mockChessGame));

        int gameId2 = 2;
        gameDAO.createGame(new GameData(gameId2, "whiteUser2", "blackUser2", "Game2", mockChessGame));

        var gamesList = gameDAO.listGames();

        assertFalse(gamesList.isEmpty());
        assertTrue(gamesList.size() >= 2);
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        // Setup
        int gameId = 3;
        GameData originalGameData = new GameData(gameId, "whiteUser", "blackUser", "OriginalGame", mockChessGame);
        gameDAO.createGame(originalGameData);

        GameData updatedGameData = new GameData(gameId, "newWhiteUser", "newBlackUser", "UpdatedGame", mockChessGame);

        // Operation
        gameDAO.updateGame(gameId, updatedGameData);

        // Assertion
        GameData retrieved = gameDAO.getGame(gameId);
        assertNotNull(retrieved);
        assertEquals("UpdatedGame", retrieved.gameName());
        assertEquals("newWhiteUser", retrieved.whiteUsername());
        assertEquals("newBlackUser", retrieved.blackUsername());
    }

    @Test
    public void testGameNameExists() throws DataAccessException {
        // Setup
        int gameId = 4;
        String gameName = "UniqueGame";
        gameDAO.createGame(new GameData(gameId, "whiteUser", "blackUser", gameName, mockChessGame));

        // Operation & Assertion
        assertTrue(gameDAO.gameNameExists(gameName));
        assertFalse(gameDAO.gameNameExists("NonExistentGameName"));
    }
}
