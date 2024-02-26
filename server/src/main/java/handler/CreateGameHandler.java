package handler;

import com.google.gson.Gson;
import service.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;
import model.GameData;

public class CreateGameHandler implements Route {
    private CreateGameService createGameService;
    private Gson gson;

    public CreateGameHandler(CreateGameService createGameService, Gson gson) {
        this.createGameService = createGameService;
        this.gson = gson;
    }

    public Object handle(Request req, Response res) {
        try {
            // Authorization check
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new SimpleResponse("Error: unauthorized"));
            }

            // Extract game creation details from request body
            CreateGameRequest gameRequest = gson.fromJson(req.body(), CreateGameRequest.class);
            if (gameRequest == null || gameRequest.gameName == null || gameRequest.gameName.trim().isEmpty()) {
                res.status(400);
                return gson.toJson(new SimpleResponse("Error: bad request"));
            }

            // Perform game creation
            GameData newGame = createGameService.createGame(gameRequest.gameName, authToken);
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
}
