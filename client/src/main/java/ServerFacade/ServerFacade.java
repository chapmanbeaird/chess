package ServerFacade;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFacade {

    private final String serverBaseUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }

    public AuthData register(UserData userData) throws ServerFacadeException {
        return makeRequest("POST", "/register", userData, AuthData.class);
    }

    public AuthData login(String username, String password) throws ServerFacadeException {
        return makeRequest("POST", "/login", new UserData(username, password, null), AuthData.class);
    }

    public List<GameData> listGames() throws ServerFacadeException {
        return List.of(makeRequest("GET", "/listgames", null, GameData[].class)); // Wrapping array in List
    }

    public GameData createGame(String gameName) throws ServerFacadeException {
        return makeRequest("POST", "/creategame", new GameData(-1, null, null, gameName, null), GameData.class);
    }

    public GameData joinGame(int gameId, String playerColor) throws ServerFacadeException {
        Map<String, Object> joinGameRequest = new HashMap<>();
        joinGameRequest.put("gameId", gameId);
        joinGameRequest.put("playerColor", playerColor);
        joinGameRequest.put("authToken", authToken); // Assuming the server requires an auth token for this action

        return makeRequest("POST", "/joingame", joinGameRequest, GameData.class);
    }

    public void logout(String authToken) throws ServerFacadeException {
        Map<String, String> logoutRequest = new HashMap<>();
        logoutRequest.put("authToken", authToken);

        makeRequest("POST", "/logout", logoutRequest, Void.class);    }

    private <T> T makeRequest(String method, String path, Object requestData, Class<T> responseClass) throws ServerFacadeException {
        try {
            URL url = new URI(serverBaseUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (requestData != null) {
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
                try (OutputStream os = http.getOutputStream()) {
                    byte[] input = gson.toJson(requestData).getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }
            http.connect();
            checkResponseCode(http);

            return responseClass == Void.class ? null : readResponseBody(http, responseClass);
        } catch (Exception e) {
            throw new ServerFacadeException(e.getMessage(), e);
        }
    }

    private void checkResponseCode(HttpURLConnection http) throws IOException, ServerFacadeException {
        int status = http.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new ServerFacadeException("HTTP error code: " + status, null);
        }
    }

    private <T> T readResponseBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(http.getInputStream(), "utf-8")) {
            return gson.fromJson(reader, responseClass);
        }
    }

    // Exception class for handling ServerFacade-related errors
    public static class ServerFacadeException extends Exception {
        public ServerFacadeException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}
