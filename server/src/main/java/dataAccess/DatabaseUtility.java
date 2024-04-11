package dataAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtility {
    private final static String CHECK_IF_EMPTY = "SELECT COUNT(*) AS rowcount FROM games";

    public static boolean isEmpty() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_IF_EMPTY)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("rowcount");
                return count == 0; // Return true if no users exist
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while checking if users table is empty", e);
        }
        return true; // Default to true
    }
}
