package handler;

import com.google.gson.Gson;
import service.JoinGameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final JoinGameService joinGameService;
    private final Gson gson;

    public JoinGameHandler(JoinGameService joinGameService, Gson gson) {
        this.joinGameService = joinGameService;
        this.gson = gson;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            // Authorization check
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse("Error: unauthorized"));
            }

            // Parse the request body
            JoinGameRequest joinRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            if (joinRequest == null || joinRequest.gameID <= 0) {
                res.status(400); // Bad Request
                return gson.toJson(new SimpleResponse("Error: bad request"));
            }

            // Join the game
            joinGameService.joinGame(joinRequest.gameID, joinRequest.playerColor, authToken);
            res.status(200); // OK
            return gson.toJson(new SimpleResponse("Joined game successfully"));

        } catch (IllegalArgumentException e) {
            res.status(403); // Forbidden
            return gson.toJson(new SimpleResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse("Error: " + e.getMessage()));
        }
    }

    private static class JoinGameRequest {
        String playerColor; // "WHITE", "BLACK", or null (for observer)
        int gameID;
    }
}

