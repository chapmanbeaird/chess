package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currTeamTurn = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {
        this.board = new ChessBoard();
        this.currTeamTurn = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (team == TeamColor.WHITE){
            currTeamTurn = TeamColor.WHITE;
        }
        else{
            currTeamTurn = TeamColor.BLACK;
        }
    }



    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            return null;
        }

        //this gets all the possible moves for a piece not including if it's in check or will be put in check
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new HashSet<>();
        for (ChessMove move : possibleMoves){
            ChessBoard originalBoard = new ChessBoard(this.board);
            try {
                makeMove(move);

                if (!isInCheck(piece.getTeamColor())){
                    validMoves.add(move);
                }
            }
            catch (InvalidMoveException e) {
                throw new RuntimeException(e);
            }
            finally {
                this.board = originalBoard;
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece.PieceType promPiece = move.getPromotionPiece();
        ChessPiece piece = board.getPiece(startPos);

        if (!validMoves(startPos).contains(move)){
            throw new InvalidMoveException("move is not a valid move");
        }

        board.addPiece(endPos, piece);
        board.addPiece(startPos, null);

        currTeamTurn = (currTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
