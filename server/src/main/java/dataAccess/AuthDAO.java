package dataAccess;

import model.AuthData;

public interface AuthDAO {

    void createAuthToken(AuthData authToken) throws DataAccessException ;

    AuthData getAuthToken(String token);

    String getUsername(String token) ;

    boolean isEmpty();

    void clearAuthTokens();

    void deleteAuthToken(String token) throws DataAccessException ;
}
