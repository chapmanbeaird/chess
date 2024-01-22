package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;


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
        return type;
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
        Collection<ChessMove> validMoves = new HashSet<>();
        if (piece == null) {
            return validMoves;
        }
        switch (piece.getPieceType()) {
            case KING: {
                int[][] directions = new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                for (int i = 0; i < directions.length; i++) {
                    int row = myPosition.getRow();
                    int col = myPosition.getColumn();
                    int[] direction = directions[i];
                    row += direction[0];
                    col += direction[1];
                    ChessPosition newPos = new ChessPosition(row, col);
                    if (!board.isValidPos(newPos)) {
                        continue;
                    }
                    ChessPiece atNewPos = board.getPiece(newPos);
                    if (atNewPos != null) {
                        if (atNewPos.getTeamColor() != piece.getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPos, null));
                        }
                        continue;
                    }
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }
            case QUEEN:{
                int[][] directions = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
                for (int i = 0; i < directions.length; i++) {
                    int row = myPosition.getRow();
                    int col = myPosition.getColumn();
                    int[] direction = directions[i];
                    while (true) {
                        row += direction[0];
                        col += direction[1];
                        ChessPosition newPos = new ChessPosition(row, col);
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
                    int row = myPosition.getRow();
                    int col = myPosition.getColumn();
                    int[] direction = directions[i];
                    while (true) {
                        row += direction[0];
                        col += direction[1];
                        ChessPosition newPos = new ChessPosition(row, col);
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
                    int row = myPosition.getRow();
                    int col = myPosition.getColumn();
                    int[] direction = directions[i];
                    row += direction[0];
                    col += direction[1];
                    ChessPosition newPos = new ChessPosition(row, col);
                    if (!board.isValidPos(newPos)) {
                        continue;
                    }
                    ChessPiece atNewPos = board.getPiece(newPos);
                    if (atNewPos != null) {
                        if (atNewPos.getTeamColor() != piece.getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPos, null));
                        }
                        continue;
                    }
                    validMoves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }

            case ROOK: {
                int[][] directions = new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
                for (int i = 0; i < directions.length; i++) {
                    int row = myPosition.getRow();
                    int col = myPosition.getColumn();
                    int[] direction = directions[i];
                    while (true) {
                        row += direction[0];
                        col += direction[1];
                        ChessPosition newPos = new ChessPosition(row, col);
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
                int direction = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
                int startRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
                int promotionRow = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
                int row = myPosition.getRow();
                int col = myPosition.getColumn();
                ChessPiece.PieceType[] promotionPieces = {PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK, PieceType.QUEEN};

                ChessPosition oneStep = new ChessPosition(row + direction, col);
                if (board.isValidPos(oneStep) && board.getPiece(oneStep) == null){
                    if (oneStep.getRow() == promotionRow){
                        for (ChessPiece.PieceType promotionPiece : promotionPieces){
                            validMoves.add(new ChessMove(myPosition, oneStep, promotionPiece));
                        }
                    }
                    else {
                        validMoves.add(new ChessMove(myPosition, oneStep, null));
                    }
                    if (row == startRow){
                        ChessPosition twoStep = new ChessPosition(row + direction * 2, col);
                        if (board.isValidPos(twoStep) && board.getPiece(twoStep) == null){
                            validMoves.add(new ChessMove(myPosition, twoStep, null));
                        }

                    }
                }
                //check if capture is available
                int[] captureOffests = {-1, 1};
                for (int offset : captureOffests){
                    ChessPosition capturePos = new ChessPosition(row + direction, col + offset);
                    if (board.isValidPos(capturePos)){
                        ChessPiece atCapPos = board.getPiece(capturePos);
                        if (atCapPos != null && atCapPos.getTeamColor() != piece.getTeamColor()){
                            if (capturePos.getRow() == promotionRow){
                                for (ChessPiece.PieceType promotionPiece : promotionPieces){
                                    validMoves.add(new ChessMove(myPosition, capturePos, promotionPiece));
                                }
                            }
                            else{
                            validMoves.add(new ChessMove(myPosition, capturePos, null));
                            }
                        }
                    }
                }
                break;
            }
        }
        return validMoves;
        }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}

