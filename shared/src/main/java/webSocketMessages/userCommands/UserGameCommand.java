package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    public UserGameCommand(String authToken) {
        this.authToken = authToken;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private final String authToken;

    public static String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }

    //Command to join as a player with a specific color.
    public class JoinPlayerCommand extends UserGameCommand {
        private int gameID;
        private ChessGame.TeamColor playerColor;

        public JoinPlayerCommand(String authToken, int gameID, ChessGame.TeamColor playerColor) {
            super(authToken);
            this.gameID = gameID;
            this.playerColor = playerColor;
            this.commandType = CommandType.JOIN_PLAYER;
        }

        public int getGameID() {
            return gameID;
        }

        public ChessGame.TeamColor getPlayerColor() {
            return playerColor;
        }
    }

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

    //Command to resign from the game.
    public class ResignCommand extends UserGameCommand {
        private int gameID;

        public ResignCommand(String authToken, int gameID) {
            super(authToken);
            this.gameID = gameID;
            this.commandType = CommandType.RESIGN;
        }

        public int getGameID() {
            return gameID;
        }
    }
}
