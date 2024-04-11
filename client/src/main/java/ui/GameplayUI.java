package ui;

import ServerFacade.ServerFacade;
import Websocket.ClientChessMessageHandler;
import Websocket.WebSocketClient;
import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import webSocketMessages.userCommands.MakeMoveCommand;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;

public class GameplayUI {
    private final WebSocketClient webSocketClient;
    private ServerFacade serverFacade;
    private Scanner scanner;
    private String authToken;
    private int gameId;
    private boolean isPlayerWhite;
    private ChessBoard currentBoard;

    public GameplayUI(ServerFacade serverFacade, String authToken, int gameId, boolean isPlayerWhite) throws DeploymentException, URISyntaxException, IOException  {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.authToken = authToken;
        this.gameId = gameId;
        this.isPlayerWhite = isPlayerWhite;
        ClientChessMessageHandler messageHandler = new ClientChessMessageHandler(this);
        webSocketClient = new WebSocketClient("ws://localhost:8080", messageHandler);
    }

    public void displayMenu() throws IOException {
        // Menu loop for gameplay options
        while (true) {
            System.out.println("\nGameplay Menu:");
            System.out.println("1. Redraw Chess Board");
            System.out.println("2. Make Move");
            System.out.println("3. Highlight Legal Moves");
            System.out.println("4. Resign");
            System.out.println("5. Leave");
            System.out.println("6. Help");
            System.out.print("Select an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    redrawChessBoard();
                    break;
                case "2":
                    makeMove();
                    break;
                case "3":
                    highlightLegalMoves();
                    break;
                case "4":
                    resign();
                    break;
                case "5":
                    leave();
                    return; // Exit loop to go back to Post-Login UI
                case "6":
                    help();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void redrawChessBoard() {
        if (currentBoard != null) {
            PrintBoard.printCurrBoard(currentBoard);
        } else {
            System.out.println("The board is not available.");
        }
    }

    public void updateGame(ChessBoard board){
        if (board != null) {
            this.currentBoard = board;
            System.out.println("Game board updated. Redrawing...");
            redrawChessBoard();
        } else {
            System.err.println("Attempted to update with null board.");
        }
    }

    private void makeMove() throws IOException {
        System.out.println("Enter position of piece you want to move (e.g., e2):");
        ChessPosition startPos = stringToChessPosition(scanner.nextLine());

        System.out.println("Enter position of where you want to move (e.g., e4):");
        ChessPosition endPos = stringToChessPosition(scanner.nextLine());

        ChessPiece.PieceType promotionPiece = null;

        // Check if the move is a pawn reaching the opposite end for promotion
        if (endPos.getRow() == 1 || endPos.getRow() == 8) {
            promotionPiece = promptForPromotionPiece();
        }
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameId, new ChessMove(startPos, endPos, promotionPiece));

        webSocketClient.sendUserGameCommand(makeMoveCommand);

        System.out.println("Move made successfully.");

        redrawChessBoard();
    }

    private ChessPosition stringToChessPosition(String positionStr) {
        if (positionStr.length() != 2) {
            return null; // Invalid input
        }
        int col = positionStr.charAt(0) - 'a' + 1; // Convert 'a' to 'h' into 1 to 8
        int row = positionStr.charAt(1);
        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType promptForPromotionPiece() {
        System.out.println("Promote pawn to (Q/R/B/N): ");
        String input = scanner.nextLine().toUpperCase();
        switch (input) {
            case "Q": return ChessPiece.PieceType.QUEEN;
            case "R": return ChessPiece.PieceType.ROOK;
            case "B": return ChessPiece.PieceType.BISHOP;
            case "N": return ChessPiece.PieceType.KNIGHT;
            default:
                System.out.println("Invalid choice. Promoting to queen by default.");
                return ChessPiece.PieceType.QUEEN;
        }
    }


    private void resign() {
        System.out.println("Are you sure you want to resign? (Y/N)");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("Y")) {
            serverFacade.resignGame(gameId, authToken);
            System.out.println("You have resigned from the game.");
            leave();
        }
    }

    private void highlightLegalMoves() {
        System.out.println("Enter the position of the piece to highlight legal moves:");
        String position = scanner.nextLine();
        List<String> moves = serverFacade.highlightLegalMoves(gameId, position, authToken);
        moves.forEach(move -> System.out.println("Legal move: " + move));
        // Implement how to highlight these moves
    }

    private void leave() {
        // Implementation here
    }

    private void help() {
        System.out.println("Available commands:");
        System.out.println("Redraw Chess Board - Redraws the chess board.");
        System.out.println("Make Move - Input your move in algebraic notation (e.g., e4, Nf3).");
        System.out.println("Highlight Legal Moves - Enter a piece's position to see its legal moves.");
        System.out.println("Resign - Forfeit the game.");
        System.out.println("Leave - Return to the main menu.");
        System.out.println("Help - Show this help message.");
    }


}
