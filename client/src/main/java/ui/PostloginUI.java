package ui;

import ServerFacade.ServerFacade;
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
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createGame() {
        // Implement game creation logic
    }

    private void joinGame() {
        // Implement game joining logic
    }

    private void listGames() {
        // Implement game listing logic
    }

    private void logout() {
        // Implement logout logic using serverFacade.logout
    }

    private void help() {
        System.out.println("Available commands:");
        System.out.println("Create Game - Start a new game.");
        System.out.println("Join Game - Join an existing game.");
        System.out.println("List Games - Show all available games.");
        System.out.println("Logout - Log out of your account.");
        System.out.println("Help - Show this help message.");
    }
}
