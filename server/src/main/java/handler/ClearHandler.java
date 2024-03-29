package handler;

import static spark.Spark.*;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.*;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final ClearService clearService;
    private final Gson gson;

    public ClearHandler(ClearService clearService, Gson gson) {
        this.clearService = clearService;
        this.gson = gson;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            clearService.clear(); // Call the clear service
            res.status(200); // HTTP OK
            return gson.toJson(new SimpleResponse("Data cleared successfully"));
        } catch (DataAccessException e) {
            res.status(500); // Internal Server Error
            return gson.toJson(new SimpleResponse("Failed to clear data: " + e.getMessage()));
        }
    }
}
