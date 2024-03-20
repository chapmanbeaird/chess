package ui;

import ServerFacade.ServerFacade;
import model.GameData;

import java.util.List;
import java.util.Scanner;
public class PostloginUI {
    private ServerFacade serverFacade;
    private Scanner scanner;
    private String authToken;

    public PostloginUI(ServerFacade serverFacade, String authToken) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
        this.authToken = authToken;
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\nPost-Login Menu:");
            System.out.println("1. Create Game");
            System.out.println("2. Join Game");
            System.out.println("3. List Games");
            System.out.println("4. Logout");
            System.out.println("5. Help");
            System.out.println("6. Observe Game");
            System.out.print("Select an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    createGame();
                    break;
                case "2":
                    joinGame();
                    break;
                case "3":
                    listGames();
                    break;
                case "4":
                    logout();
                    return; // since logout transitions back to PreloginUI
                case "5":
                    help();
                    break;
                case "6":
                    observeGame();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }


    private void createGame() {
        System.out.println("Enter a name for the new game:");
        String gameName = scanner.nextLine();

        try {
            GameData gameData = serverFacade.createGame(gameName, authToken);
            System.out.println("Game created successfully with ID: " + gameData.gameID());
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Error creating game: " + e.getMessage());
        }
    }


    private void joinGame() {
        System.out.println("Enter the ID of the game you want to join:");
        int gameId;
        try {
            gameId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.err.println("Invalid game ID format. Please enter a numeric ID.");
            return; // Exit the method if the game ID is not a valid number
        }

        System.out.println("Choose your color:");
        System.out.println("1. White");
        System.out.println("2. Black");
        String playerColor = scanner.nextLine();

        // Convert numeric input to color
        String color;
        switch (playerColor) {
            case "1":
                color = "WHITE";
                break;
            case "2":
                color = "BLACK";
                break;
            default:
                System.err.println("Invalid option. Please enter 1 for White or 2 for Black.");
                return;

        }
        try {
            GameData gameData = serverFacade.joinGame(gameId, color, authToken);
            System.out.println("Joined game " + gameId + " as " + color);
            PrintBoard.printChessBoards();

        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Error joining game: " + e.getMessage());
        }
    }



    private void listGames() {
        try {
            List<GameData> games = serverFacade.listGames(authToken);
            if (games.isEmpty()) {
                System.out.println("There are no active games at the moment.");
            } else {
                System.out.println("Active games:");
                for (GameData game : games) {
                    System.out.println("ID: " + game.gameID() + ", Name: " + game.gameName() +
                            ", White: " + game.whiteUsername() + ", Black: " + game.blackUsername());
                }
            }
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Error listing games: " + e.getMessage());
        }
    }

    private void observeGame() {
        System.out.println("Enter the ID of the game you want to observe:");
        int gameId = Integer.parseInt(scanner.nextLine());

        try {
            GameData gameData = serverFacade.joinGameAsObserver(gameId, authToken);
            System.out.println("Now observing game " + gameId);
            PrintBoard.printChessBoards();
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Error joining game as observer: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid game ID format. Please enter a numeric ID.");
        }
    }


    private void logout() {
        try {
            serverFacade.logout(authToken);
            System.out.println("You have been logged out.");
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Failed to logout: " + e.getMessage());
        }
    }


    private void help() {
        System.out.println("Available commands:");
        System.out.println("Create Game - Start a new game.");
        System.out.println("Join Game - Join an existing game.");
        System.out.println("List Games - Show all available games.");
        System.out.println("Logout - Log out of your account.");
        System.out.println("Help - Show this help message.");
        System.out.println("Observe Game - Join a game as an observer");
    }
}
