package handler;

import com.google.gson.Gson;
import dataAccess.mysqlAuthDAO;
import model.AuthData;
import model.GameData;
import service.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class ListGamesHandler implements Route {
    private ListGamesService listGamesService;
    private Gson gson;
    private mysqlAuthDAO authDAO;

    public ListGamesHandler(ListGamesService listGamesService, Gson gson, mysqlAuthDAO authDAO) {
        this.listGamesService = listGamesService;
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
                return gson.toJson(new ListGamesHandler.SimpleResponse("Error: unauthorized"));
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
    private static class SimpleResponse {
        String message;

        public SimpleResponse(String message) {
            this.message = message;
        }
    }
}
