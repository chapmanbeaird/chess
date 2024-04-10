package webSocketMessages.serverMessages;

import chess.ChessGame;

/**
 * Message sent by the server to load the current game state.
 */
public class LoadGameMessage extends ServerMessage {
    private ChessGame game;

    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

}
