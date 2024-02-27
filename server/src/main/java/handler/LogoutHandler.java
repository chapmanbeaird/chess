package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import service.LogoutService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final LogoutService logoutService;
    private final Gson gson;
    private final AuthDAO authDAO;

    public LogoutHandler(LogoutService logoutService, Gson gson, AuthDAO authDAO) {
        this.logoutService = logoutService;
        this.gson = gson;
        this.authDAO = authDAO;
    }

    @Override
    public Object handle(Request req, Response res) throws DataAccessException {
        try {
            // Extract the authToken from the database and req header
            String reqAuthToken = req.headers("Authorization");
            AuthData daoAuthData = authDAO.getAuthToken(reqAuthToken);
            if (daoAuthData == null || daoAuthData.authToken() == null) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse("Error: unauthorized"));
            }

            // Perform the logout
            logoutService.logoutUser(daoAuthData.authToken());
            res.status(200); // OK
            return gson.toJson(new SimpleResponse("Logout successful"));

        } catch (DataAccessException e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse("Error: " + e.getMessage()));
        }
    }

    private static class SimpleResponse {
        String message;

        public SimpleResponse(String message) {
            this.message = message;
        }
    }
}
