package ServiceTests;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ListGamesService;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ListGamesTest {
    private ListGamesService listGamesService;
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new GameDAO();
        listGamesService = new ListGamesService(gameDAO);

        // Clearing existing games to start fresh for each test
        gameDAO.clearGames();
    }

    @Test
    public void listGames_EmptyList() throws DataAccessException {
        // Test to ensure an empty list is returned when no games exist
        List<GameData> games = listGamesService.listGames();
        assertTrue(games.isEmpty(), "Expected an empty list when no games are present.");
    }

    @Test
    public void listGames_NonEmptyList() throws DataAccessException {
        // Adding a couple of games
        gameDAO.createGame(new GameData(1, "player1", "player2", "Game1", null));
        gameDAO.createGame(new GameData(2, "player3", "player4", "Game2", null));

        // Test to ensure the service lists all games correctly
        List<GameData> games = listGamesService.listGames();
        assertEquals(2, games.size(), "Expected a list with 2 games.");
    }
}
