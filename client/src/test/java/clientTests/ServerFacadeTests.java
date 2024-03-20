package clientTests;

import ServerFacade.ServerFacade;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static final String BASE_URL = "http://localhost:";
    private static ServerFacade facade;
    private static int port;
    private static Server server;
    private static String validAuthToken;
    private static final String INVALID_AUTH_TOKEN = "invalidAuthToken";

    @BeforeAll
    public static void init() {
        // Start the server on a random available port.
        server = new Server();
        port = server.run(0);

        // Initialize the ServerFacade with the base URL
        facade = new ServerFacade(BASE_URL + port);
    }
    @BeforeEach
    public void setupAuthData() throws ServerFacade.ServerFacadeException {
        // Clear the database before each test
        facade.clearDatabase();

        // Register a new user and store the valid auth token
        UserData userData = new UserData("testUser", "testPass", "testEmail@example.com");
        AuthData authData = facade.register(userData);
        validAuthToken = authData.authToken();
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void registerPositive() {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = assertDoesNotThrow(() -> facade.register(userData),
                "Register should not throw exceptions for valid input");
        assertNotNull(authData.authToken(), "Auth token should not be null");
    }

    @Test
    void registerNegative() {
        UserData userData = new UserData("", "", ""); // Invalid user data
        assertThrows(ServerFacade.ServerFacadeException.class, () -> facade.register(userData),
                "Register should throw an exception for invalid input");
    }

    @Test
    void loginPositive() throws ServerFacade.ServerFacadeException {
        // Assuming user "player2" is already registered in the database
        UserData userData = new UserData("player2", "password2", "email2@email.com");
        facade.register(userData);
        assertDoesNotThrow(() -> facade.login("player2", "password2"),
                "Login should not throw exceptions for valid credentials");
    }

    @Test
    void loginNegative() {
        // Assuming user "nonexistent" is not registered
        assertThrows(ServerFacade.ServerFacadeException.class, () -> facade.login("nonexistent", "wrongpassword"),
                "Login should throw an exception for invalid credentials");
    }

    @Test
    void listGamesPositive() {
        List<GameData> games = assertDoesNotThrow(() -> facade.listGames(validAuthToken),
                "List games should not throw exceptions for a valid auth token");
        assertNotNull(games, "The returned games list should not be null");
    }

    @Test
    void listGamesNegative() {
        assertThrows(ServerFacade.ServerFacadeException.class, () -> facade.listGames(INVALID_AUTH_TOKEN),
                "List games should throw an exception for an invalid auth token");
    }

    @Test
    void createGamePositive() {
        GameData gameData = assertDoesNotThrow(() -> facade.createGame("testGame", validAuthToken),
                "Create game should not throw exceptions for a valid auth token");
        assertNotNull(gameData, "Game data should not be null");
    }

    @Test
    void createGameNegative() {
        assertThrows(ServerFacade.ServerFacadeException.class, () -> facade.createGame("testGame", INVALID_AUTH_TOKEN),
                "Create game should throw an exception for an invalid auth token");
    }

    @Test
    void joinGamePositive() {
        // Create a game to join first
        GameData createdGame = assertDoesNotThrow(() -> facade.createGame("testGame", validAuthToken),
                "Create game should not throw exceptions for a valid auth token");
        assertNotNull(createdGame, "Game creation should be successful");

        int gameID = createdGame.gameID();

        GameData gameData = assertDoesNotThrow(() -> facade.joinGame(gameID, "WHITE", validAuthToken),
                "Join game should not throw exceptions for valid input");
        assertNotNull(gameData, "Game data should not be null after joining");
    }
    @Test
    void joinGameNegative() {
        int invalidGameID = -1;
        assertThrows(ServerFacade.ServerFacadeException.class, () -> facade.joinGame(invalidGameID, "WHITE", INVALID_AUTH_TOKEN),
                "Join game should throw an exception for invalid input");
    }
    @Test
    void joinGameAsObserverPositive() {
        // first ensure there's a game to observe
        GameData createdGame = assertDoesNotThrow(() -> facade.createGame("observableGame", validAuthToken),
                "Creating game for observation should succeed");

        int gameID = createdGame.gameID();

        GameData gameData = assertDoesNotThrow(() -> facade.joinGameAsObserver(gameID, validAuthToken),
                "Joining game as observer should not throw exceptions for valid input");
        assertNotNull(gameData, "Game data should not be null after joining as observer");
    }
    @Test
    void joinGameAsObserverNegative() {
        int invalidGameID = -1;
        assertThrows(ServerFacade.ServerFacadeException.class, () -> facade.joinGameAsObserver(invalidGameID, INVALID_AUTH_TOKEN),
                "Join game as observer should throw an exception for invalid input");
    }
    @Test
    void logoutPositive() {
        assertDoesNotThrow(() -> facade.logout(validAuthToken),
                "Logout should not throw exceptions for a valid auth token");
    }
    @Test
    void logoutNegative() {
        assertThrows(ServerFacade.ServerFacadeException.class, () -> facade.logout(INVALID_AUTH_TOKEN),
                "Logout should throw an exception for an invalid auth token");
    }

}