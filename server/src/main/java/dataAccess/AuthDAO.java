package dataAccess;

import model.AuthData;

public interface AuthDAO {

    void createAuthToken(AuthData authToken) throws DataAccessException ;

    AuthData getAuthToken(String token) throws DataAccessException;

    String getUsername(String token) throws DataAccessException;

    boolean isEmpty() throws DataAccessException;

    void clearAuthTokens() throws DataAccessException;

    void deleteAuthToken(String token) throws DataAccessException ;
}
