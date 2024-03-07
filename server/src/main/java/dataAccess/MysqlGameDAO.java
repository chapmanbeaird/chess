package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MysqlGameDAO {

    private final static String CREATE_GAME = "INSERT INTO games (...) VALUES (...)";
    private final static String GET_GAME = "SELECT * FROM games WHERE gameID = ?";
    private final static String LIST_GAMES = "SELECT * FROM games";
    private final static String UPDATE_GAME = "UPDATE games SET ... WHERE gameID = ?";
    private final static String CLEAR_GAMES = "DELETE FROM games";
    private final static String CHECK_GAME_NAME = "SELECT 1 FROM games WHERE gameName = ?";
    private Gson gson = new Gson();



    public void createGame(GameData game) throws DataAccessException {
        // Use SQL INSERT statement to add game
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_GAME, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game())); // converts ChessGame object to json
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting new game into the database", e);
        }
    }

    public GameData getGame(int gameId) throws DataAccessException {
        // Use SQL SELECT statement to get game
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_GAME)) {

            stmt.setInt(1, gameId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                return new GameData(id, whiteUsername, blackUsername, gameName, game);
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while retrieving game", e);
        }
    }

    public List<GameData> listGames() throws DataAccessException{
        // Use SQL SELECT statement to list games
        List<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(LIST_GAMES);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                games.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while listing games", e);
        }
        return games;
    }

    public void updateGame(int gameId, GameData updatedGame) throws DataAccessException {
        // Use SQL UPDATE statement to update game
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_GAME)) {

            stmt.setString(1, updatedGame.whiteUsername());
            stmt.setString(2, updatedGame.blackUsername());
            stmt.setString(3, updatedGame.gameName());
            stmt.setString(4, gson.toJson(updatedGame.game())); //coverts chessgame to json
            stmt.setInt(5, gameId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while updating game", e);
        }
    }

    public void clearGames() throws DataAccessException{
        // Use SQL DELETE statement to clear games
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CLEAR_GAMES)) {
                stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing games", e);
        }
    }

    public boolean gameNameExists(String gameName) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_GAME_NAME)) {

            stmt.setString(1, gameName);
            ResultSet rs = stmt.executeQuery();

            // If the ResultSet is not empty, it means a game with that name exists
            return rs.next();

        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while checking if game name exists", e);
        }
    }

    public boolean isEmpty() throws DataAccessException {
        final String CHECK_IF_EMPTY = "SELECT COUNT(*) AS rowcount FROM users";
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
