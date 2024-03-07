package dataAccess;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();

    public void createGame(GameData game) throws DataAccessException {
        if (games.containsKey(game.gameID())){
            throw new DataAccessException("Game already exists");
        }
        games.put(game.gameID(), game);
    }

    public GameData getGame(int gameId) throws DataAccessException {
        if (!games.containsKey(gameId)){
            throw new DataAccessException("Game does not exist");
        }
        GameData game = games.get(gameId);
        return game;
    }

    public List<GameData> listGames(){
        return new ArrayList<>(games.values());
    }

    public void updateGame(int gameId, GameData updatedGame) throws DataAccessException {
        if (!games.containsKey(gameId)) {
            throw new DataAccessException("Game not found.");
        }
        games.put(gameId, updatedGame);
    }

    public boolean gameNameExists(String gameName) throws DataAccessException{
        return games.values().stream().anyMatch(game -> gameName.equals(game.gameName()));
    }

    public boolean isEmpty(){
        return games.isEmpty();
    }

    public void clearGames(){
        games.clear();
    }
}
