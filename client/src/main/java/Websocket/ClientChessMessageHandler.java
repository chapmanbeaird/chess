package Websocket;

import chess.ChessGame;
import com.google.gson.*;
import ui.GameplayUI;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

import java.lang.reflect.Type;


public class ClientChessMessageHandler implements WebSocketClient.ChessMessageHandler {
    private GameplayUI gameplayUI;
    private Gson gson = new GsonBuilder().registerTypeAdapter(LoadGameMessage.class, new LoadGameMessageDeserializer())
            .create();


    // Constructor to accept a reference to your UI or game state manager
    public ClientChessMessageHandler(GameplayUI gameplayUI) {
        this.gameplayUI = gameplayUI;
    }
    @Override
    public void handleServerMessage(ServerMessage message) {
//        System.out.println("Received message type: " + message.getServerMessageType());
        // Check that rawJson is not null
        if (message.getRawJson() == null) {
            System.err.println("Raw JSON string is null. Cannot deserialize.");
            return;
        }
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                // Update your client's game state or UI with the new game data
                LoadGameMessage loadGameMessage = gson.fromJson(message.getRawJson(), LoadGameMessage.class); // Deserialize JSON into LoadGameMessage
//                System.out.println("LoadGameMessage received. Updating game state...");
                ChessGame game = loadGameMessage.getGame();
                updateGame(game);
                break;
            case ERROR:
                // Handle errors
                ErrorMessage errorMessage = gson.fromJson(message.getRawJson(), ErrorMessage.class);
                showError(errorMessage.getErrorMessage());
                break;
            case NOTIFICATION:
                // Show notifications
                NotificationMessage notificationMessage = gson.fromJson(message.getRawJson(), NotificationMessage.class);
                showNotification(notificationMessage.getMessage());
                break;
        }
    }

    private void updateGame(ChessGame game) {
        if (game != null && game.getBoard() != null) {
//            System.out.println("Updating GameplayUI with new board.");
            gameplayUI.updateGame(game.getBoard());
        } else {
            System.err.println("Received game or board is null.");
        }
    }

    private void showError(String errorMessage) {
        System.err.println("Error: " + errorMessage);
    }

    private void showNotification(String message) {
        System.out.println("Notification: " + message);
    }


    class LoadGameMessageDeserializer implements JsonDeserializer<LoadGameMessage> {
        @Override
        public LoadGameMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Assume "game" is the JSON field where the ChessGame data is stored
            JsonElement gameElement = jsonObject.get("game");
            ChessGame game = context.deserialize(gameElement, ChessGame.class);

            // Assuming LoadGameMessage has a constructor that accepts a ChessGame object
            return new LoadGameMessage(game);
        }
    }
}

