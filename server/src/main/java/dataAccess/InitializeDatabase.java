package dataAccess;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class InitializeDatabase {
    private static String jdbcUrl;
    private static String username;
    private static String password;

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
            "gameState TEXT" +
            ");";
    private static final String CREATE_AUTH_TABLE = "CREATE TABLE IF NOT EXISTS auth_tokens (" +
            "tokenID INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
            "authToken VARCHAR(255) NOT NULL UNIQUE, " +
            "username VARCHAR(50) NOT NULL" +
            ");";

    private static void loadDatabaseProperties() {
        // Load properties file from classpath
        try (InputStream input = InitializeDatabase.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                throw new RuntimeException("Unable to find db.properties");
            }
            prop.load(input);

            String host = prop.getProperty("db.host");
            String port = prop.getProperty("db.port");
            String name = prop.getProperty("db.name");

            jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + name;
            username = prop.getProperty("db.user");
            password = prop.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load database properties", e);
        }
    }
    public void start() throws DataAccessException {
        //load in the credentials from db.properties
        loadDatabaseProperties();
        // Ensure the database exists
        DatabaseManager.createDatabase();

        // Connect to the database and create tables
        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement()) {
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
