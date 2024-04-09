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
        if (userData == null) {
            throw new ServerFacadeException("User data cannot be null.", null);
        }
        if (userData.username() == null || userData.username().trim().isEmpty()) {
            throw new ServerFacadeException("Username cannot be empty.", null);
        }
        if (userData.password() == null || userData.password().trim().isEmpty()) {
            throw new ServerFacadeException("Password cannot be empty.", null);
        }
        if (userData.email() == null || !isValidEmail(userData.email())) {
            throw new ServerFacadeException("Invalid email address.", null);
        }
        return makeRequest("POST", "/user", userData, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws ServerFacadeException {
        return makeRequest("POST", "/session", new UserData(username, password, null), AuthData.class, null);
    }

    public List<GameData> listGames(String authToken) throws ServerFacadeException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        GameListResponse response = makeRequest("GET", "/game", null, GameListResponse.class, headers);
        return response.getGames(); // This extracts the list from the response object.
    }



    public GameData createGame(String gameName, String authToken) throws ServerFacadeException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("gameName", gameName);
        return makeRequest("POST", "/game", requestBody, GameData.class, headers);
    }



    public GameData joinGame(int gameID, String playerColor, String authToken) throws ServerFacadeException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        Map<String, Object> joinGameRequest = new HashMap<>();
        joinGameRequest.put("playerColor", playerColor);
        joinGameRequest.put("gameID", gameID);
        return makeRequest("PUT", "/game", joinGameRequest, GameData.class, headers);
    }


    public GameData joinGameAsObserver(int gameID, String authToken) throws ServerFacadeException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        Map<String, Object> joinObserverRequest = new HashMap<>();
        joinObserverRequest.put("gameID", gameID);
        return makeRequest("PUT", "/game", joinObserverRequest, GameData.class, headers);
    }



    public void logout(String authToken) throws ServerFacadeException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authToken);
        makeRequest("DELETE", "/session", null, Void.class, headers);
    }
    public void clearDatabase() throws ServerFacadeException {
        makeRequest("DELETE", "/db", null, Void.class, null);
    }



    public <T> T makeRequest(String method, String path, Object requestData, Class<T> responseClass, Map<String, String> headers) throws ServerFacadeException {
        try {
            URL url = new URI(serverBaseUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    http.setRequestProperty(header.getKey(), header.getValue());
                }
            }
            if (requestData != null) {
                http.setDoOutput(true);
                http.addRequestProperty("Content-Type", "application/json");
                try (OutputStream os = http.getOutputStream()) {
                    var input = gson.toJson(requestData);
                    os.write(input.getBytes("utf-8"));
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
    private boolean isValidEmail(String email) {
        // Simple regex to check email validity
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public class GameListResponse {
        private List<GameData> games;

        // Getters and setters
        public List<GameData> getGames() {
            return games;
        }

        public void setGames(List<GameData> games) {
            this.games = games;
        }
    }


}
