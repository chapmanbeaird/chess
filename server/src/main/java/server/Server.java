package server;

import com.google.gson.Gson;
import dataAccess.*;
import handler.*;
import service.*;
import spark.Spark;

import static spark.Spark.*;


public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        initializeDatabase();

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Initialize services
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

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
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);
        ListGamesService listGamesService = new ListGamesService(gameDAO);

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
        // Call the start method to create the database and tables if they don't exist
        dbInitializer.start();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
