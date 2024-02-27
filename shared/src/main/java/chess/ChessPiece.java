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
        switch (this.type) {
            case KING:
                return getSingleStepMoves(board, myPosition, new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}}, true);
            case QUEEN:
                return getSlidingMoves(board, myPosition, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}});
            case BISHOP:
                return getSlidingMoves(board, myPosition, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
            case KNIGHT:
                return getSingleStepMoves(board, myPosition, new int[][]{{-2, -1}, {-2, 1}, {2, -1}, {2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}}, false);
            case ROOK:
                return getSlidingMoves(board, myPosition, new int[][]{{0, 1}, {0, -1}, {-1, 0}, {1, 0}});
            case PAWN:
                return getPawnMoves(board, myPosition);
            default:
                return new HashSet<>();
        }
    }


    private Collection<ChessMove> getSingleStepMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, boolean singleStep) {
        Collection<ChessMove> validMoves = new HashSet<>();
        for (int[] direction : directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];
            addMoveIfValid(board, myPosition, row, col, validMoves, singleStep);
        }
        return validMoves;
    }

    private Collection<ChessMove> getSlidingMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> validMoves = new HashSet<>();
        for (int[] direction : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            while (true) {
                row += direction[0];
                col += direction[1];
                if (!addMoveIfValid(board, myPosition, row, col, validMoves, true)) {
                    break;
                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new HashSet<>();
        int direction = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (getTeamColor() == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece.PieceType[] promotionPieces = {PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT};

        // Single step forward
        ChessPosition oneStep = new ChessPosition(row + direction, col);
        if (board.isValidPos(oneStep) && board.getPiece(oneStep) == null) {
            if (oneStep.getRow() == promotionRow) {
                for (ChessPiece.PieceType promotionPiece : promotionPieces) {
                    validMoves.add(new ChessMove(myPosition, oneStep, promotionPiece));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, oneStep, null));
            }

            // Double step forward from start position
            if (row == startRow) {
                ChessPosition twoStep = new ChessPosition(row + 2 * direction, col);
                if (board.isValidPos(twoStep) && board.getPiece(twoStep) == null) {
                    validMoves.add(new ChessMove(myPosition, twoStep, null));
                }
            }
        }

        // Capture moves
        int[] captureOffsets = {-1, 1};
        for (int offset : captureOffsets) {
            ChessPosition capturePos = new ChessPosition(row + direction, col + offset);
            if (board.isValidPos(capturePos)) {
                ChessPiece atCapturePos = board.getPiece(capturePos);
                if (atCapturePos != null && atCapturePos.getTeamColor() != getTeamColor()) {
                    if (capturePos.getRow() == promotionRow) {
                        for (ChessPiece.PieceType promotionPiece : promotionPieces) {
                            validMoves.add(new ChessMove(myPosition, capturePos, promotionPiece));
                        }
                    } else {
                        validMoves.add(new ChessMove(myPosition, capturePos, null));
                    }
                }
            }
        }

        return validMoves;
    }

    private boolean addMoveIfValid(ChessBoard board, ChessPosition myPosition, int newRow, int newCol, Collection<ChessMove> validMoves, boolean breakOnOccupied) {
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        if (!board.isValidPos(newPos)) {
            return false;
        }
        ChessPiece atNewPos = board.getPiece(newPos);
        if (atNewPos != null) {
            if (atNewPos.getTeamColor() != this.pieceColor) {
                validMoves.add(new ChessMove(myPosition, newPos, null));
            }
            return !breakOnOccupied;
        }
        validMoves.add(new ChessMove(myPosition, newPos, null));
        return true;
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

