package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MysqlGameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void testCreateGameWithExistingID() throws DataAccessException {
        // Setup
        int gameId = 1;
        GameData gameData = new GameData(gameId, "whiteUser", "blackUser", "TestGame", mockChessGame);
        gameDAO.createGame(gameData);

        // Operation & Assertion
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(gameData), "Expected DataAccessException when creating a game with existing gameID.");
    }
    @Test
    public void testListGamesEmpty() throws DataAccessException {
        // Assuming clearGames has successfully cleared all entries
        gameDAO.clearGames();

        // Operation
        List<GameData> gamesList = gameDAO.listGames();

        // Assertion
        assertTrue(gamesList.isEmpty(), "Game list should be empty after clearing all games.");
    }
    @Test
    public void testUpdateNonExistingGame() {
        // Setup
        int nonExistentGameId = 999; // Assuming this ID does not exist
        GameData updatedGameData = new GameData(nonExistentGameId, "newWhiteUser", "newBlackUser", "UpdatedGame", mockChessGame);

        // Operation & Assertion
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(nonExistentGameId, updatedGameData), "Expected DataAccessException when updating a non-existent game.");
    }
    @Test
    public void testClearGames() throws DataAccessException {
        // Setup
        gameDAO.createGame(new GameData(1, "whiteUser", "blackUser", "TestGame", mockChessGame));

        // Operation
        gameDAO.clearGames();

        // Assertion
        assertTrue(gameDAO.isEmpty(), "Games table should be empty after clearGames operation.");
    }
    @Test
    public void testIsNotEmpty() throws DataAccessException {
        // Setup: Creating a game should ensure the games table is not empty
        gameDAO.createGame(new GameData(1, "whiteUser", "blackUser", "TestGame", mockChessGame));

        // Operation & Assertion
        assertFalse(gameDAO.isEmpty(), "Games table should not be empty after adding a game.");
    }


}
