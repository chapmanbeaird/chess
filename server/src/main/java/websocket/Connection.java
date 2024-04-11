package websocket;

import org.eclipse.jetty.websocket.api.Session;

public class Connection {
    public Integer gameID;
    public Session session;
    public String playerName;


    public Connection(Integer gameID, Session session, String playerName) {
        this.gameID = gameID;
        this.session = session;
        this.playerName = playerName;
    }
}
