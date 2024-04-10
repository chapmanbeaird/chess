package Websocket;

import chess.ChessGame;
import webSocketMessages.serverMessages.ServerMessage;

public class ClientChessMessageHandler implements WebSocketClient.ChessMessageHandler {
    @Override
    public void handleServerMessage(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                // Update your client's game state or UI with the new game data
                ServerMessage.LoadGameMessage loadGameMessage = (ServerMessage.LoadGameMessage) message;
                ChessGame game = loadGameMessage.getGame();
                updateGame(game);
                break;
            case ERROR:
                // Handle errors
                ServerMessage.ErrorMessage errorMessage = (ServerMessage.ErrorMessage) message;
                showError(errorMessage.getErrorMessage());
                break;
            case NOTIFICATION:
                // Show notifications
                ServerMessage.NotificationMessage notificationMessage = (ServerMessage.NotificationMessage) message;
                showNotification(notificationMessage.getMessage());
                break;
        }
    }

    private void updateGame(ChessGame game) {
        // Implement your game state update logic here
    }

    private void showError(String errorMessage) {
        // Implement your error handling UI here
    }

    private void showNotification(String message) {
        // Implement notification display here
    }
}

