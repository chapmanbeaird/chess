package ServiceTests;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.JoinGameService;

import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTest {
    private JoinGameService joinGameService;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        joinGameService = new JoinGameService(gameDAO, authDAO);

        gameDAO.clearGames();
        authDAO.clearAuthTokens();
    }

    @Test
    public void testJoinGameAsWhitePlayer() throws DataAccessException {
        // Initializing
        int gameId = 1;
        String playerColor = "WHITE";
        String authToken = "authToken123";
        String username = "User123";
        gameDAO.createGame(new GameData(gameId, null, null, "ChessGame1", null));
        authDAO.createAuthToken(new AuthData(authToken, username));

        // join game
        joinGameService.joinGame(gameId, playerColor, authToken);

        // assert game exists and username is correct
        GameData game = gameDAO.getGame(gameId);
        assertNotNull(game);
        assertEquals(username, game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void testJoinGameAsBlackPlayer() throws DataAccessException {
        // initializing
        int gameId = 2;
        String playerColor = "BLACK";
        String authToken = "authToken456";
        String username = "User456";
        gameDAO.createGame(new GameData(gameId, null, null, "ChessGame2", null));
        authDAO.createAuthToken(new AuthData(authToken, username));

        // join the game
        joinGameService.joinGame(gameId, playerColor, authToken);

        // assert the game exists and username is correct
        GameData game = gameDAO.getGame(gameId);
        assertNotNull(game);
        assertEquals(username, game.blackUsername());
        assertNull(game.whiteUsername());
    }

    @Test
    public void testJoinGameSpotAlreadyTaken() throws DataAccessException {
        // initializing
        int gameId = 3;
        String playerColor = "WHITE";
        String authToken = "authToken789";
        String existingPlayerAuthToken = "existingAuthToken";
        String existingPlayer = "ExistingUser";
        gameDAO.createGame(new GameData(gameId, existingPlayer, null, "ChessGame3", null));
        authDAO.createAuthToken(new AuthData(existingPlayerAuthToken, existingPlayer)); // Existing player
        authDAO.createAuthToken(new AuthData(authToken, "NewUser")); // New player

        // make sure it throws an error
        assertThrows(DataAccessException.class, () -> {
            joinGameService.joinGame(gameId, playerColor, authToken);
        }, "Should throw exception if player spot is already taken.");
    }
}
