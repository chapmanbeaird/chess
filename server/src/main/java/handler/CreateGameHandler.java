package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import model.AuthData;
import service.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;
import model.GameData;

public class CreateGameHandler implements Route {
    private CreateGameService createGameService;
    private Gson gson;
    private AuthDAO authDAO;

    public CreateGameHandler(CreateGameService createGameService, Gson gson, AuthDAO authDAO) {
        this.createGameService = createGameService;
        this.gson = gson;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {

            // Extract the authToken from the database and req header
            String reqAuthToken = req.headers("Authorization");
            AuthData daoAuthData = authDAO.getAuthToken(reqAuthToken);
            if (daoAuthData == null || daoAuthData.authToken() == null) {
                res.status(401); // Unauthorized
                return gson.toJson(new CreateGameHandler.SimpleResponse("Error: unauthorized"));
            }

            // Extract game creation details from request body
            CreateGameRequest gameRequest = gson.fromJson(req.body(), CreateGameRequest.class);
            if (gameRequest == null || gameRequest.gameName == null || gameRequest.gameName.trim().isEmpty()) {
                res.status(400);
                return gson.toJson(new SimpleResponse("Error: bad request"));
            }

            // Perform game creation
            GameData newGame = createGameService.createGame(gameRequest.gameName);
            res.status(200);
            return gson.toJson(new CreateGameResponse(newGame.gameID()));

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new SimpleResponse("Error: " + e.getMessage()));
        }
    }


    private static class CreateGameResponse {
        int gameID;

        public CreateGameResponse(int gameID) {
            this.gameID = gameID;
        }
    }

    private static class CreateGameRequest {
        private String gameName;
    }
    private static class SimpleResponse {
        String message;

        public SimpleResponse(String message) {
            this.message = message;
        }
    }
}
