package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.LogoutService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final LogoutService logoutService;
    private final Gson gson;

    public LogoutHandler(LogoutService logoutService, Gson gson) {
        this.logoutService = logoutService;
        this.gson = gson;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            // Extract the authToken from the request header
            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(400); // Bad Request
                return gson.toJson(new SimpleResponse("Error: invalid request"));
            }

            // Perform the logout
            logoutService.logoutUser(authToken);
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
