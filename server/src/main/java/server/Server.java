package server;

import handler.*;
import service.*;
import spark.*;
import com.google.gson.Gson;
import dataAccess.UserDAO;
import dataAccess.GameDAO;
import dataAccess.AuthDAO;
import static spark.Spark.delete;
import static spark.Spark.post;
import static spark.Spark.put;


public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // Initialize services
        UserDAO userDAO = new UserDAO();
        GameDAO gameDAO = new GameDAO();
        AuthDAO authDAO = new AuthDAO();

        // Initialize Gson for JSON parsing
        Gson gson = new Gson();

        // Register routes
        setupRoutes(gson, userDAO, gameDAO, authDAO);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void setupRoutes(Gson gson, UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);
        RegisterService registerService = new RegisterService(userDAO);
        CreateGameService createGameService = new CreateGameService(gameDAO);
        JoinGameService joinGameService = new JoinGameService(gameDAO, authDAO);
        LoginService loginService = new LoginService(userDAO, authDAO);
        LogoutService logoutService = new LogoutService(authDAO);

        //Clear route
        delete("/db", new ClearHandler(clearService, gson));

        // User Registration
        post("/user", new RegisterHandler(registerService, gson));

        //Create Game
        post("/game", new CreateGameHandler(createGameService, gson));

        //Join Game
        put("/game", new JoinGameHandler(joinGameService, gson));

        //Login
        post("/session", new LoginHandler(loginService, gson));

        //Logout
        delete("/session", new LogoutHandler(logoutService, gson));
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
