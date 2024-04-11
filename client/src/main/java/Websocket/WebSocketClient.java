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

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message){
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                System.out.println("Raw JSON received: " + message); // Debug print the received JSON
                serverMessage.setRawJson(message);
                System.out.println("ServerMessage after setting rawJson: " + serverMessage.getRawJson()); // Verify that rawJson is set
                messageHandler.handleServerMessage(serverMessage);
        }
        });
    }
    private void connectToServer() throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    // Send a UserGameCommand to the server
    public void sendUserGameCommand(UserGameCommand command) throws IOException {
        String message = gson.toJson(command);
        this.session.getBasicRemote().sendText(message);
    }

}
