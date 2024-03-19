package ui;

import ServerFacade.ServerFacade;
import model.AuthData;
import model.UserData;

import java.util.Scanner;


public class PreloginUI {
    private ServerFacade serverFacade;
    private Scanner scanner;

    public PreloginUI(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\nPre-Login Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Help");
            System.out.println("4. Quit");
            System.out.print("Select an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    register();
                    break;
                case "2":
                    login();
                    break;
                case "3":
                    help();
                    break;
                case "4":
                    quit();
                    return; // Exiting the loop and the method ends
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void register() {
        System.out.println("Enter a username:");
        String username = scanner.nextLine();

        System.out.println("Enter a password:");
        String password = scanner.nextLine();

        System.out.println("Enter an email:");
        String email = scanner.nextLine();

        try {
            UserData userData = new UserData(username, password, email);
            AuthData authData = serverFacade.register(userData);

            if (authData != null && authData.authToken() != null) {
                System.out.println("Registration successful. Auth token: " + authData.authToken());
            } else {
                System.out.println("Registration failed. The username might already be taken or the email is in use.");
            }
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
    }


    private void login() {
        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        System.out.println("Enter your password:");
        String password = scanner.nextLine();

        try {
            AuthData authData = serverFacade.login(username, password);

            if (authData != null && authData.authToken() != null) {
                System.out.println("Login successful.");
                PostloginUI postloginUI = new PostloginUI(serverFacade, authData.authToken());
                postloginUI.displayMenu();
            } else {
                System.out.println("Login failed. Check your username and password and try again.");
            }
        } catch (ServerFacade.ServerFacadeException e) {
            System.err.println("Error during login: " + e.getMessage());
        }
    }


    private void help() {
        System.out.println("Available commands:");
        System.out.println("Register - Create a new account.");
        System.out.println("Login - Log into an existing account.");
        System.out.println("Help - Show this help message.");
        System.out.println("Quit - Exit the application.");
    }

    private void quit() {
        System.out.println("Exiting the application...");
    }
}
