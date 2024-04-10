package webSocketMessages.userCommands;

//Command to join as an observer
public class JoinObserverCommand extends UserGameCommand {
    private int gameID;

    public JoinObserverCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

    public int getGameID() {
        return gameID;
    }
}
