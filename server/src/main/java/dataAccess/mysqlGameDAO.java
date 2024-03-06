package dataAccess;

import model.GameData;
import java.sql.*;
import java.util.List;

public class mysqlGameDAO {

    private final static String CREATE_GAME = "INSERT INTO games (...) VALUES (...)";
    private final static String GET_GAME = "SELECT * FROM games WHERE gameID = ?";
    private final static String LIST_GAMES = "SELECT * FROM games";
    private final static String UPDATE_GAME = "UPDATE games SET ... WHERE gameID = ?";

    public void createGame(GameData game) throws DataAccessException {
        // Use SQL INSERT statement to add game
    }

    public GameData getGame(int gameId) throws DataAccessException {
        // Use SQL SELECT statement to get game
    }

    public List<GameData> listGames(){
        // Use SQL SELECT statement to list games
    }

    public void updateGame(int gameId, GameData updatedGame) throws DataAccessException {
        // Use SQL UPDATE statement to update game
    }

    public void clearGames(){
        // Use SQL DELETE statement to clear games
    }
}
