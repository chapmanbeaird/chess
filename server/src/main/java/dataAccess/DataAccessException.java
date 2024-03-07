package dataAccess;

/**
 * Indicates there was an error connecting to the database.
 */
public class DataAccessException extends Exception {
    // Constructor with message and cause for exception chaining
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Overloaded constructor with only a message for when there's no exception to chain
    public DataAccessException(String message) {
        super(message);
    }
}
