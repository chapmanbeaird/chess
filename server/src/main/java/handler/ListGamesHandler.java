package handler;

import com.google.gson.Gson;
import service.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;
import model.GameData;
import java.util.List;

public class ListGamesHandler implements Route {
    private ListGamesService listGamesService;
    private Gson gson;

    public ListGamesHandler(ListGamesService listGamesService, Gson gson) {
        this.listGamesService = listGamesService;
        this.gson = gson;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            // Authorization check
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson(new SimpleResponse("Error: unauthorized"));
            }

            // Fetch the list of games
            List<GameData> games = listGamesService.listGames();
            res.status(200);
            return gson.toJson(new ListGamesResponse(games));

        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new SimpleResponse("Error: " + e.getMessage()));
        }
    }

    private static class ListGamesResponse {
        List<GameData> games;

        public ListGamesResponse(List<GameData> games) {
            this.games = games;
        }
    }
}
