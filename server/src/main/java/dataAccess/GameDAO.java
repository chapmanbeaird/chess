package dataAccess;

import model.GameData;

import java.util.List;

public interface GameDAO {

    public void createGame(GameData game) throws DataAccessException;

    public GameData getGame(int gameId) throws DataAccessException ;

    public List<GameData> listGames();

    public void updateGame(int gameId, GameData updatedGame) throws DataAccessException ;

    public boolean gameNameExists(String gameName) throws DataAccessException;

    public boolean isEmpty();

    public void clearGames();
}
