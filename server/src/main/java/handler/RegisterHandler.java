package handler;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import model.UserData;
import service.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private RegisterService registerService;
    private Gson gson;

    public RegisterHandler(RegisterService registerService, Gson gson) {
        this.registerService = registerService;
        this.gson = gson;
    }

    @Override
    public Object handle(Request request, Response response) {
        try {
            UserData userData = gson.fromJson(request.body(), UserData.class);

            if (userData == null || userData.username() == null || userData.password() == null || userData.email() == null) {
                response.status(400); // Bad Request
                return gson.toJson(new SimpleResponse("Error: bad request"));
            }

            String authToken = registerService.registerUser(userData);

            if (authToken != null) {
                response.status(200);
                return gson.toJson(new RegistrationResult(userData.username(), authToken));
            } else {
                response.status(403);
                return gson.toJson(new SimpleResponse("Error: already taken"));
            }
        } catch (Exception e) {
            response.status(500);
            return gson.toJson(new SimpleResponse("Error: " + e.getMessage()));
        }
    }

    private record RegistrationResult(String username, String authToken) {
    }

}
