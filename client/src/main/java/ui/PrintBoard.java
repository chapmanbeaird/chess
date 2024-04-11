package ui;

import chess.ChessBoard;
import chess.ChessPiece;

public class PrintBoard {

    public static void printStartingChessBoards() {
        String[][] board = new String[8][8];

        // Initialize the board with empty squares
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EscapeSequences.EMPTY;
            }
        }

        // Setup the pieces on the board for White at the bottom
        setupChessBoard(board, true);
        System.out.println("Board with White at the bottom:");
        printBoard(board);

        System.out.flush();
        System.out.println();

        // Setup the pieces on the board for Black at the bottom
        setupChessBoard(board, false);
        System.out.println("Board with Black at the bottom:");
        printBoard(board);
        System.out.flush();

    }

    public static void printCurrBoard(ChessBoard chessBoard) {
        String[][] boardVisual = new String[8][8];
        ChessPiece[][] board = chessBoard.getBoard();

        // Initialize the boardVisual with empty squares
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardVisual[i][j] = EscapeSequences.EMPTY;
            }
        }

        // Populate the boardVisual array based on the ChessBoard state
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = board[row][col];
                if (piece != null) {
                    // Convert the ChessPiece to its corresponding symbol
                    boardVisual[row][col] = pieceToSymbol(piece);
                }
            }
        }

        printBoard(boardVisual);
    }

    private static String pieceToSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        switch (piece.getTeamColor()) {
            case WHITE:
                switch (piece.getPieceType()) {
                    case PAWN: return EscapeSequences.WHITE_PAWN;
                    case ROOK: return EscapeSequences.WHITE_ROOK;
                    case KNIGHT: return EscapeSequences.WHITE_KNIGHT;
                    case BISHOP: return EscapeSequences.WHITE_BISHOP;
                    case QUEEN: return EscapeSequences.WHITE_QUEEN;
                    case KING: return EscapeSequences.WHITE_KING;
                    default: return "?";
                }
            case BLACK:
                switch (piece.getPieceType()) {
                    case PAWN: return EscapeSequences.BLACK_PAWN;
                    case ROOK: return EscapeSequences.BLACK_ROOK;
                    case KNIGHT: return EscapeSequences.BLACK_KNIGHT;
                    case BISHOP: return EscapeSequences.BLACK_BISHOP;
                    case QUEEN: return EscapeSequences.BLACK_QUEEN;
                    case KING: return EscapeSequences.BLACK_KING;
                    default: return "?";
                }
            default: return "?";
        }
    }

    private static void setupChessBoard(String[][] board, boolean whiteAtBottom) {
        String[] whiteBackRow = {
                EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN,
                EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK
        };
        String[] blackBackRow = {
                EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN,
                EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK
        };

        for (int i = 0; i < 8; i++) {
            if (whiteAtBottom) {
                // Set white pieces at the bottom
                board[6][i] = EscapeSequences.WHITE_PAWN; // White pawns
                board[7][i] = whiteBackRow[i]; // White back row

                // Set black pieces at the top
                board[1][i] = EscapeSequences.BLACK_PAWN; // Black pawns
                board[0][i] = blackBackRow[i]; // Black back row
            } else {
                // Set black pieces at the bottom
                board[6][i] = EscapeSequences.BLACK_PAWN; // Black pawns
                board[7][i] = blackBackRow[i]; // Black back row

                // Set white pieces at the top
                board[1][i] = EscapeSequences.WHITE_PAWN; // White pawns
                board[0][i] = whiteBackRow[i]; // White back row
            }
        }
    }


    private static void printBoard(String[][] board) {
        String whiteColor = EscapeSequences.SET_TEXT_COLOR_WHITE;
        String blackColor = EscapeSequences.SET_TEXT_COLOR_BLACK;
        String lightSquare = EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        String darkSquare = EscapeSequences.SET_BG_COLOR_DARK_GREY;
        String resetColor = EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.RESET_BG_COLOR;


        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                // Alternate the background color
                String bgColor = ((row + col) % 2 == 0) ? lightSquare : darkSquare;

                // Check if the square has a white or black piece and set the text color accordingly
                String piece = board[row][col];
                String fgColor;
                if (piece.equals(EscapeSequences.WHITE_PAWN) || piece.equals(EscapeSequences.WHITE_ROOK) ||
                        piece.equals(EscapeSequences.WHITE_KNIGHT) || piece.equals(EscapeSequences.WHITE_BISHOP) ||
                        piece.equals(EscapeSequences.WHITE_QUEEN) || piece.equals(EscapeSequences.WHITE_KING)) {
                    // White piece
                    fgColor = whiteColor;
                } else if (piece.equals(EscapeSequences.BLACK_PAWN) || piece.equals(EscapeSequences.BLACK_ROOK) ||
                        piece.equals(EscapeSequences.BLACK_KNIGHT) || piece.equals(EscapeSequences.BLACK_BISHOP) ||
                        piece.equals(EscapeSequences.BLACK_QUEEN) || piece.equals(EscapeSequences.BLACK_KING)) {
                    // Black piece
                    fgColor = blackColor;
                } else {
                    // Empty square or something that is not a piece
                    fgColor = bgColor;
                }
                System.out.print(bgColor + fgColor + piece + EscapeSequences.RESET_ALL);
            }
            System.out.println(EscapeSequences.RESET_ALL);

        }
        // Reset colors after the board is printed
        System.out.println(EscapeSequences.RESET_ALL);
    }




}
