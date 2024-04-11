package Websocket;

import com.google.gson.Gson;
//import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
//import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
//import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
@ClientEndpoint
public class WebSocketClient extends Endpoint {

    private Session session;
    private Gson gson = new Gson();
    private ChessMessageHandler messageHandler;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
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

//    @OnWebSocketConnect
//    public void onOpen(Session session) {
//        this.session = session;
//    }

//    @OnMessage
//    public void onMessage(ServerMessage message) {
//        messageHandler.handleServerMessage(message);
//    }
    @OnMessage
    public void onMessage(String messageJson, Session session) {
        ServerMessage serverMessage = gson.fromJson(messageJson, ServerMessage.class);
        System.out.println("Raw JSON received: " + messageJson); // Debug print the received JSON
        serverMessage.setRawJson(messageJson);
        System.out.println("ServerMessage after setting rawJson: " + serverMessage.getRawJson()); // Verify that rawJson is set
        this.messageHandler.handleServerMessage(serverMessage);
    }

    // Send a UserGameCommand to the server
    public void sendUserGameCommand(UserGameCommand command) throws IOException {
        String message = gson.toJson(command);
        this.session.getBasicRemote().sendText(message);
    }

    // Close the WebSocket connection
    @OnClose
    public void closeConnection() throws IOException {
//        this.session.close();
        this.session = null;
    }


}
