package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (piece == null) {
            return validMoves;
        }
        switch (piece.getPieceType()) {
            case KING: {
                int[][] directions = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                for (int i = 0; i < directions.length; i++) {
                    int x = myPosition.getColumn();
                    int y = myPosition.getRow();
                    int[] direction = directions[i];
                    x += direction[0];
                    y += direction[1];
                    ChessPosition newPos = new ChessPosition(x, y);
                    if (!board.isValidPos(newPos)) {
                        continue;
                    }
                    ChessPiece atNewPos = board.getPiece(newPos);
                    if (atNewPos != null) {
                        if (atNewPos.getTeamColor() != piece.getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }
            case QUEEN:{
                int[][] directions = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
                for (int i = 0; i < directions.length; i++) {
                    int x = myPosition.getColumn();
                    int y = myPosition.getRow();
                    int[] direction = directions[i];
                    while (true) {
                        x += direction[0];
                        y += direction[1];
                        ChessPosition newPos = new ChessPosition(x, y);
                        if (!board.isValidPos(newPos)) {
                            break;
                        }
                        ChessPiece atNewPos = board.getPiece(newPos);
                        if (atNewPos != null) {
                            if (atNewPos.getTeamColor() != piece.getTeamColor()) {
                                validMoves.add(new ChessMove(myPosition, newPos, null));
                            }
                            break;
                        }
                        validMoves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
                break;
            }
            case BISHOP:{
                int[][] directions = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                for (int i = 0; i < directions.length; i++) {
                    int x = myPosition.getColumn();
                    int y = myPosition.getRow();
                    int[] direction = directions[i];
                    while (true) {
                        x += direction[0];
                        y += direction[1];
                        ChessPosition newPos = new ChessPosition(x, y);
                        if (!board.isValidPos(newPos)) {
                            break;
                        }
                        ChessPiece atNewPos = board.getPiece(newPos);
                        if (atNewPos != null) {
                            if (atNewPos.getTeamColor() != piece.getTeamColor()) {
                                validMoves.add(new ChessMove(myPosition, newPos, null));
                            }
                            break;
                        }
                        validMoves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
                break;
            }
            case KNIGHT:{
                int[][] directions = new int[][]{{-2, -1}, {-2, 1}, {2, -1}, {2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}};
                for (int i = 0; i < directions.length; i++) {
                    int x = myPosition.getColumn();
                    int y = myPosition.getRow();
                    int[] direction = directions[i];
                    x += direction[0];
                    y += direction[1];
                    ChessPosition newPos = new ChessPosition(x, y);
                    if (!board.isValidPos(newPos)) {
                        continue;
                    }
                    ChessPiece atNewPos = board.getPiece(newPos);
                    if (atNewPos != null) {
                        if (atNewPos.getTeamColor() != piece.getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }

            case ROOK: {
                int[][] directions = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
                for (int i = 0; i < directions.length; i++) {
                    int x = myPosition.getColumn();
                    int y = myPosition.getRow();
                    int[] direction = directions[i];
                    while (true) {
                        x += direction[0];
                        y += direction[1];
                        ChessPosition newPos = new ChessPosition(x, y);
                        if (!board.isValidPos(newPos)) {
                            break;
                        }
                        ChessPiece atNewPos = board.getPiece(newPos);
                        if (atNewPos != null) {
                            if (atNewPos.getTeamColor() != piece.getTeamColor()) {
                                validMoves.add(new ChessMove(myPosition, newPos, null));
                            }
                            break;
                        }
                        validMoves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
                break;
            }
            case PAWN:{
                int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? -1 : 1;
                int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 6 : 1;
                int x = myPosition.getColumn();
                int y = myPosition.getRow();

                ChessPosition oneStep = new ChessPosition(x, y+direction);
                if (board.isValidPos(oneStep) && board.getPiece(oneStep) == null){
                    validMoves.add(new ChessMove(myPosition, oneStep, null));

                    if (y == startRow){
                        ChessPosition twoStep = new ChessPosition(x, y+direction+1);
                        if (board.isValidPos(twoStep) && board.getPiece(twoStep) == null){
                            validMoves.add(new ChessMove(myPosition, twoStep, null));
                        }

                    }
                }
                //check if capture is available
                int[] captureOffests = {-1, 1};
                for (int offset : captureOffests){
                    ChessPosition capturePos = new ChessPosition(x + offset, y + direction);
                    if (board.isValidPos(capturePos)){
                        ChessPiece atCapPos = board.getPiece(capturePos);
                        if (atCapPos != null && atCapPos.getTeamColor() != piece.getTeamColor()){
                            validMoves.add(new ChessMove(myPosition, capturePos, null));
                        }
                    }
                }
                break;
            }
        }
        return null;
        }
    }

