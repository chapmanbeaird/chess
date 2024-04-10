package websocket;

import com.google.gson.Gson;
import webSocketMessages.serverMessages.ServerMessage;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChessConnectionManager {
    // Map each game ID to a set of Connection objects
    private final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session, String playerName) {
        // Create a new Connection for the player
        Connection connection = new Connection(gameID, session, playerName);

        // Update the map with the new player's connection
        connections.compute(gameID, (key, existingConnections) -> {
            // If there's already a set of connections for this game, use it, otherwise create a new set
            Set<Connection> updatedConnections = (existingConnections != null) ? existingConnections : ConcurrentHashMap.newKeySet();
            updatedConnections.add(connection);
            return updatedConnections;
        });
    }

    public void remove(int gameID, String playerName) {
        // Remove the connection associated with the player from the game
        connections.getOrDefault(gameID, ConcurrentHashMap.newKeySet()).removeIf(connection -> connection.playerName.equals(playerName));
    }

    public void broadcast(int gameID, String excludePlayerName, ServerMessage message) throws IOException {
        Set<Connection> gameConnections = connections.getOrDefault(gameID, ConcurrentHashMap.newKeySet());

        String jsonMessage = new Gson().toJson(message);

        gameConnections.stream()
                .filter(connection -> connection.session.isOpen() && !connection.playerName.equals(excludePlayerName))
                .forEach(connection -> {
                    try {
                        connection.session.getRemote().sendString(jsonMessage); // Send the message as a string
                    } catch (IOException e) {
                        // Handle the exception
                        e.printStackTrace();
                    }
                });
    }

}
