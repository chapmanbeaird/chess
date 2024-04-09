package ui;

import ServerFacade.ServerFacade;

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
        // This should request the current game state from the server and redraw the board
        // For now, just simulate this by calling PrintBoard.printChessBoards();
        PrintBoard.printChessBoards();
    }

    private void makeMove() {
        // Implementation here
    }

    private void highlightLegalMoves() {
        // Implementation here
    }

    private void resign() {
        // Implementation here
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
