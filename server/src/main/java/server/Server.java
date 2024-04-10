package server;

import com.google.gson.Gson;
import dataAccess.*;
import handler.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.*;
import spark.Spark;
import websocket.ChessWebSocketHandler;

import static spark.Spark.*;


public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        initializeDatabase();

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Initialize services
        UserDAO userDAO = new MysqlUserDAO();
        GameDAO gameDAO = new MysqlGameDAO();
        AuthDAO authDAO = new MysqlAuthDAO();

        // Initialize Gson for JSON parsing
        Gson gson = new Gson();

        // Register routes
        setupRoutes(gson, userDAO, gameDAO, authDAO);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void setupRoutes(Gson gson, UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        CreateGameService createGameService = new CreateGameService(gameDAO);
        JoinGameService joinGameService = new JoinGameService(gameDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO, new BCryptPasswordEncoder());
        LogoutService logoutService = new LogoutService(authDAO);
        ListGamesService listGamesService = new ListGamesService(gameDAO);
        ChessWebSocketHandler webSocketHandler = new ChessWebSocketHandler(gameDAO, authDAO);

        Spark.webSocket("/connect", webSocketHandler);

        //Clear route
        delete("/db", new ClearHandler(clearService, gson));

        // User Registration
        post("/user", new RegisterHandler(registerService, gson));

        //Create Game
        post("/game", new CreateGameHandler(createGameService, gson, authDAO));

        //Join Game
        put("/game", new JoinGameHandler(joinGameService, gson));

        //Login
        post("/session", new LoginHandler(loginService, gson));

        //Logout
        delete("/session", new LogoutHandler(logoutService, gson, authDAO));

        //List Games
        get("/game", new ListGamesHandler(listGamesService, gson, authDAO));

    }
    private void initializeDatabase() {
        InitializeDatabase dbInitializer = new InitializeDatabase();
        try {
            // Call the start method to create the database and tables if they don't exist
            dbInitializer.start();
        } catch (DataAccessException e) {
            // Log the exception
            e.printStackTrace();
            stop();
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
