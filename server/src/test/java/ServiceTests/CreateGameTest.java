package ServiceTests;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import chess.ChessGame;
import service.CreateGameService;

public class CreateGameTest {
    private CreateGameService createGameService;
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new GameDAO();
        createGameService = new CreateGameService(gameDAO);
    }

    @Test
    public void testCreateGameSuccess() throws DataAccessException {

        String gameName = "NewGame";
        String creatorUsername = "User123";


        GameData createdGame = createGameService.createGame(gameName, creatorUsername);


        assertNotNull(createdGame); //make sure game exists
        assertEquals(gameName, createdGame.gameName()); //make sure game names are equal
        assertEquals(creatorUsername, createdGame.whiteUsername()); // make sure creator is same as white username
        assertNull(createdGame.blackUsername(), "Black username should be null for a new game.");
        assertNull(createdGame.game(), "ChessGame should be null for a new game.");
        assertTrue(createdGame.gameID() > 0, "Game ID should be a positive integer.");
    }

    @Test
    public void testCreateGameWithExistingName() throws DataAccessException {
        // initialize
        String gameName = "ExistingGame";
        String creatorUsername = "User123";
        ChessGame chessGame = new ChessGame();
        gameDAO.createGame(new GameData(1, creatorUsername, null, gameName, chessGame)); // Adjusted for new constructor

        // make sure it throws an error
        assertThrows(DataAccessException.class, () -> {
            createGameService.createGame(gameName, "NewUser");
        }, "Creating a game with an existing name should throw DataAccessException");
    }
}
