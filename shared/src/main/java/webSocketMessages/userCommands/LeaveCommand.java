package webSocketMessages.userCommands;

//Command to leave the game
public class LeaveCommand extends UserGameCommand {
    private int gameID;

    public LeaveCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
    }

    public int getGameID() {
        return gameID;
    }
}