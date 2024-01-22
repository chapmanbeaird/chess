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
        if (position.getRow() >= 0 && position.getRow() < ROWS && position.getColumn() >= 0 && position.getColumn() < COLS){
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

    public boolean isValidPos(ChessPosition position){
        if((position.getColumn() < 0 || position.getColumn() > 7) || (position.getRow() < 0 || position.getRow() > 7)){
            return false;
        }
        else {
            return true;
        }
    };

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clear();
        ChessPiece whitePawn0 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn1 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn2 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn3 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn4 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn5 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn6 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece whitePawn7 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn0 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn1 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn2 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn3 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn4 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn5 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn6 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn7 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        ChessPiece whiteRook0 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece whiteRook1 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook0 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook1 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPiece whiteBishop0 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteBishop1 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece blackBishop0 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece blackBishop1 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        ChessPiece whiteKnight0 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteKnight1 =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackKnight0 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackKnight1 =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        ChessPiece whiteQueen =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece blackQueen =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        ChessPiece whiteKing =  new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPiece blackKing =  new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

        addPiece(new ChessPosition(0,0), blackRook0);
        addPiece(new ChessPosition(1,0), blackKnight0);
        addPiece(new ChessPosition(2,0), blackBishop0);
        addPiece(new ChessPosition(3,0), blackKing);
        addPiece(new ChessPosition(4,0), blackQueen);
        addPiece(new ChessPosition(5,0), blackBishop1);
        addPiece(new ChessPosition(6,0), blackKnight1);
        addPiece(new ChessPosition(7,0), blackRook1);
        addPiece(new ChessPosition(0,1), blackPawn0);
        addPiece(new ChessPosition(1,1), blackPawn1);
        addPiece(new ChessPosition(2,1), blackPawn2);
        addPiece(new ChessPosition(3,1), blackPawn3);
        addPiece(new ChessPosition(4,1), blackPawn4);
        addPiece(new ChessPosition(5,1), blackPawn5);
        addPiece(new ChessPosition(6,1), blackPawn6);
        addPiece(new ChessPosition(7,1), blackPawn7);

        addPiece(new ChessPosition(0,7), whiteRook0);
        addPiece(new ChessPosition(1,7), whiteKnight0);
        addPiece(new ChessPosition(2,7), whiteBishop0);
        addPiece(new ChessPosition(3,7), whiteKing);
        addPiece(new ChessPosition(4,7), whiteQueen);
        addPiece(new ChessPosition(5,7), whiteBishop1);
        addPiece(new ChessPosition(6,7), whiteKnight1);
        addPiece(new ChessPosition(7,7), whiteRook1);
        addPiece(new ChessPosition(0,6), whitePawn0);
        addPiece(new ChessPosition(1,6), whitePawn1);
        addPiece(new ChessPosition(2,6), whitePawn2);
        addPiece(new ChessPosition(3,6), whitePawn3);
        addPiece(new ChessPosition(4,6), whitePawn4);
        addPiece(new ChessPosition(5,6), whitePawn5);
        addPiece(new ChessPosition(6,6), whitePawn6);
        addPiece(new ChessPosition(7,6), whitePawn7);


























    }

    public void clear(){
        for (int i = 0; i < ROWS; i++){
            for (int j = 0; j < COLS; j++){
                board[i][j] = null;

            }
        }
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
