package dataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class InitializeDatabase {
    // Constants for JDBC URL, username, and password
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/chessdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "password";

    // SQL statements to create tables if they do not exist
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(50) UNIQUE NOT NULL, " +
            "email VARCHAR(100) UNIQUE NOT NULL, " +
            "password VARCHAR(60) NOT NULL" +
            ");";

    private static final String CREATE_GAMES_TABLE = "CREATE TABLE IF NOT EXISTS games (" +
            "gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            "whiteUsername VARCHAR(50), " +
            "blackUsername VARCHAR(50), " +
            "gameName VARCHAR(100), " +
            "gameState TEXT, " +
            ");";
    private static final String CREATE_AUTH_TABLE = "CREATE TABLE IF NOT EXISTS auth_tokens (" +
            "tokenID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            "authToken VARCHAR(255) NOT NULL UNIQUE, " +
            "username VARCHAR(50) NOT NULL, " +
            ");";

    public void start() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Execute SQL statements to create tables
            stmt.execute(CREATE_USERS_TABLE);
            stmt.execute(CREATE_GAMES_TABLE);
            stmt.execute(CREATE_AUTH_TABLE);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize the database", e);
        }
    }
}
