package dataAccess;
import model.UserData;
import java.util.HashMap;
import java.util.Map;


public class UserDAO {
    // In-memory storage for demonstration purposes
    private final Map<String, UserData> users = new HashMap<>();

    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        users.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    public boolean isEmpty(){
        return users.isEmpty();
    }

    public void clearUsers() {
        users.clear();
    }
}
