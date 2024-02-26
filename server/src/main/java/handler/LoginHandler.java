package handler;

import com.google.gson.Gson;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final LoginService loginService;
    private final Gson gson;

    public LoginHandler(LoginService loginService, Gson gson) {
        this.loginService = loginService;
        this.gson = gson;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        try {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
            if (loginRequest == null || loginRequest.username == null || loginRequest.password == null) {
                res.status(400); // Bad Request
                return gson.toJson(new SimpleResponse("Error: bad request"));
            }

            // Attempt to login the user
            String authToken = loginService.loginUser(loginRequest.username, loginRequest.password);
            if (authToken == null) {
                res.status(401); // Unauthorized
                return gson.toJson(new SimpleResponse("Error: unauthorized"));
            }

            res.status(200); // OK
            return gson.toJson(new AuthResponse(loginRequest.username, authToken));

        } catch (Exception e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse("Error: " + e.getMessage()));
        }
    }

    private static class LoginRequest {
        String username;
        String password;
    }

    private static class AuthResponse {
        String username;
        String authToken;

        public AuthResponse(String username, String authToken) {
            this.username = username;
            this.authToken = authToken;
        }
    }
}
