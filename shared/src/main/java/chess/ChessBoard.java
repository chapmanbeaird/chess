package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private static final int ROWS = 8;
    private static final int COLS = 8;
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[ROWS][COLS];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //Checks to see if it is within the bounds of the board.
        //need to check more cases. If the position is occupied already. if you can take the opponents piece at that position
        if (position.getRow() >= 0 && position.getRow() <= ROWS && position.getColumn() >= 0 && position.getColumn() <= COLS){
            board[position.getRow()][position.getColumn()] = piece;
        }

    }

    public ChessPiece[][] getBoard() {
        return board;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        //check to see if it is a valid position and return the piece that is there
        if (position.getRow() >= 0 && position.getRow() <= ROWS && position.getColumn() >= 0 && position.getColumn() <= COLS){
            return board[position.getRow()][position.getColumn()];
        }
        //if no piece at that location, return null
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.deepToString(board) +
                '}';
    }
}
