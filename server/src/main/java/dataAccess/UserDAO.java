package dataAccess;

import model.UserData;


public interface UserDAO {

    void createUser(UserData user) throws DataAccessException;

    boolean isEmailUsed(String email) throws DataAccessException;

    boolean isUsernameUsed(String username) throws DataAccessException;



    UserData getUser(String username) throws DataAccessException;

    boolean isEmpty() throws DataAccessException;

    void clearUsers() throws DataAccessException;
}
