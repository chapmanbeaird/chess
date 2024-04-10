package Websocket;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Endpoint {

    private Session session;
    private Gson gson = new Gson();
    private ChessMessageHandler messageHandler;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    // Define an interface for handling incoming messages
    public interface ChessMessageHandler {
        void handleServerMessage(ServerMessage message);
    }

    public WebSocketClient(String url, ChessMessageHandler messageHandler) throws URISyntaxException, IOException, DeploymentException {
        this.messageHandler = messageHandler;
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/connect");

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);

        this.session.addMessageHandler(String.class, (message) -> {
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            this.messageHandler.handleServerMessage(serverMessage);
        });
    }

    private void connectToServer() throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(ServerMessage message) {
        messageHandler.handleServerMessage(message);
    }

    // Send a UserGameCommand to the server
    public void sendUserGameCommand(UserGameCommand command) throws IOException {
        String message = gson.toJson(command);
        this.session.getBasicRemote().sendText(message);
    }

    // Close the WebSocket connection
    public void closeConnection() throws IOException {
        this.session.close();
    }


}
