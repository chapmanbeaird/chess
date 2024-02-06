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

    private ChessPosition whiteKingPosition;
    private ChessPosition blackKingPosition;

    public ChessGame() {
        this.board = new ChessBoard();
        this.currTeamTurn = TeamColor.WHITE;
        getKingPositions();

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
            ChessBoard copyBoard = new ChessBoard(this.board);
            //simulate a move on a copy of the board
            copyBoard.addPiece(move.getEndPosition(), copyBoard.getPiece(move.getStartPosition()));
            copyBoard.addPiece(move.getStartPosition(), null);

            //create a temporary chess game
            ChessGame tempGame = new ChessGame();
            tempGame.setBoard(copyBoard);
            tempGame.setTeamTurn(currTeamTurn);

            //if the simulated move doesn't put the king in check, add the move to the collection
            if (!tempGame.isInCheck(currTeamTurn)){
                validMoves.add(move);
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

        if (!isValidMove(move)){
            throw new InvalidMoveException("move is not a valid move");
        }

        //update the king positions
        if (piece.getPieceType() == ChessPiece.PieceType.KING){
            if (piece.getTeamColor() == TeamColor.WHITE){
                whiteKingPosition = endPos;
            }
            else {
                blackKingPosition = endPos;
            }
        }

        //move the piece
        board.addPiece(endPos, piece);
        board.addPiece(startPos, null);

        //reset the team turn
        currTeamTurn = (currTeamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public boolean isValidMove(ChessMove move){
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPos);

        //check if there isn't a piece there or if the color isn't the right turn
        if (piece == null || piece.getTeamColor() != currTeamTurn){
            return false;
        }
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPos);
        //check if the move is even in the possible moves
        if (!possibleMoves.contains(move)) {
            return false;
        }

        // Simulate the move on a copy of the board
        ChessBoard copyBoard = new ChessBoard(this.board);
        copyBoard.addPiece(endPos, copyBoard.getPiece(startPos));
        copyBoard.addPiece(startPos, null);

        // Update king position in the copy if it's a king move
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingPosition = endPos;
            } else {
                blackKingPosition = endPos;
            }
        }

        // Create a temporary chess game with the copied board
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(copyBoard);
        tempGame.setTeamTurn(currTeamTurn);

        // Check if the king is still in check after the move. If not in check then it's a valid move
        return !tempGame.isInCheck(currTeamTurn);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingPos = (teamColor == TeamColor.WHITE) ? whiteKingPosition : blackKingPosition;

        TeamColor oppColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (int row = 0 ; row < 8 ; row++){
            for (int col = 0 ; col < 8 ; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                //check to make sure there is a piece there and that it is the opponent's color
                if (piece != null && piece.getTeamColor() == oppColor){
                    //get all the moves that the opponent can make
                    Collection<ChessMove> oppPieceMoves = piece.pieceMoves(board, pos);
                    for (ChessMove move : oppPieceMoves){
                        //if one of those moves is the same as the king position, then that team is in check
                        if (move.getEndPosition().equals(kingPos)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }
        ChessPosition kingPos = (teamColor == TeamColor.WHITE) ? whiteKingPosition : blackKingPosition;
        Collection<ChessMove> kingMoves = validMoves(kingPos);

        for (ChessMove move : kingMoves){
            ChessBoard originalBoard = new ChessBoard(this.board);

            try {
                makeMove(move);
                if(!isInCheck(teamColor)){
                    this.board = originalBoard;
                    return false;
                }
            }
            catch (InvalidMoveException e) {
            }
            finally {
                this.board = originalBoard;
            }
        }
        return true;
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

    public void getKingPositions() {
        for (int row = 1 ; row <= 8 ; row++){
            for (int col = 1 ; col <= 8 ; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                //set white king position
                if (piece != null && piece.getTeamColor() == TeamColor.WHITE && piece.getPieceType() == ChessPiece.PieceType.KING){
                    whiteKingPosition = pos;
                }
                //set black king position
                if (piece != null && piece.getTeamColor() == TeamColor.BLACK && piece.getPieceType() == ChessPiece.PieceType.KING){
                    blackKingPosition = pos;
                }
            }
        }
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        getKingPositions();
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
