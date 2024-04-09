package ui;

import ServerFacade.ServerFacade;
import model.GameData;

import java.util.List;
import java.util.Scanner;

public class GameplayUI {
    private ServerFacade serverFacade;
    private Scanner scanner;
    private String authToken;
    private int gameId;
    private boolean isPlayerWhite;

    public GameplayUI(ServerFacade serverFacade, String authToken, int gameId, boolean isPlayerWhite) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.authToken = authToken;
        this.gameId = gameId;
        this.isPlayerWhite = isPlayerWhite;
    }

    public void displayMenu() {
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

    }

    private void makeMove() {
        System.out.println("Enter your move:");
        String move = scanner.nextLine();
        try {
            serverFacade.makeMove(gameId, move, authToken);
            System.out.println("Move made successfully.");
            redrawChessBoard(); // This method should fetch the latest game state and display it.
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Failed to make move: " + e.getMessage());
        }
    }

    private void resign() {
        System.out.println("Are you sure you want to resign? (Y/N)");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("Y")) {
            try {
                serverFacade.resignGame(gameId, authToken);
                System.out.println("You have resigned from the game.");
                leave();
            } catch (ServerFacade.ServerFacadeException e) {
                System.err.println("Failed to resign: " + e.getMessage());
            }
        }
    }

    private void highlightLegalMoves() {
        System.out.println("Enter the position of the piece to highlight legal moves:");
        String position = scanner.nextLine();
        try {
            List<String> moves = serverFacade.highlightLegalMoves(gameId, position, authToken);
            moves.forEach(move -> System.out.println("Legal move: " + move));
            // Implement how to highlight these moves
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Failed to highlight moves: " + e.getMessage());
        }
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
