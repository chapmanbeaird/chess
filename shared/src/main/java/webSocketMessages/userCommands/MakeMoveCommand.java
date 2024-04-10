package webSocketMessages.userCommands;

import chess.ChessMove;

//Command to make a move in the game.
public class MakeMoveCommand extends UserGameCommand {
    private int gameID;
    private ChessMove move;

    public MakeMoveCommand(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
}