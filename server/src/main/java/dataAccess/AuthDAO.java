package dataAccess;
import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    public void createAuthToken(AuthData authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken.authToken())){
            throw new DataAccessException("AuthToken already exists");
        }
        authTokens.put(authToken.authToken(), authToken);
    }

    public AuthData getAuthToken(String token) throws DataAccessException {
        if (!authTokens.containsKey(token)){
            throw new DataAccessException("AuthToken does not exist");
        }
        AuthData authData = authTokens.get(token);
        return authData;
    }

    public String getUsername(String token) throws DataAccessException {
        AuthData authData = authTokens.get(token);
        if (authData == null) {
            throw new DataAccessException("AuthToken does not exist");
        }
        return authData.username();
    }

    public boolean isEmpty(){
        return authTokens.isEmpty();
    }

    public void clearAuthTokens(){
        authTokens.clear();
    }

    public void deleteAuthToken(String token) throws DataAccessException {
        if (!authTokens.containsKey(token)){
            throw new DataAccessException("AuthToken does not exist");
        }
        authTokens.remove(token);
    }
}
