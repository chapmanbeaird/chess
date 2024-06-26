import ServerFacade.ServerFacade;
import ui.PreloginUI;

public class ClientMain {
    public static void main(String[] args) {
        // Desired port for the server
        int serverPort = 8080;

        // Base URL of the server's API
        String serverBaseUrl = "http://localhost:" + serverPort;

        // Create an instance of ServerFacade with the server's base URL
        ServerFacade serverFacade = new ServerFacade(serverBaseUrl);

        System.out.println("♕ Welcome to CS240 Chess Client!");

        // Create an instance of the PreloginUI
        PreloginUI preloginUI = new PreloginUI(serverFacade);

        // Display the pre-login menu
        preloginUI.displayMenu();

    }
}