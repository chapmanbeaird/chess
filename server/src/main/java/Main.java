import chess.*;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import handler.ClearHandler;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.Server;
import service.ClearService;

import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        Server server = new Server();
        server.run(8080);
//
//        // Start the server
//        port(8080);
//
//        UserDAO userDataAccess = new UserDAO();
//        GameDAO gameDataAccess = new GameDAO();
//        AuthDAO authDataAccess = new AuthDAO();
//
//        // Initialize services
//        ClearService clearService = new ClearService(userDataAccess, gameDataAccess, authDataAccess);
//
//        // Initialize handlers
//        ClearHandler clearHandler = new ClearHandler(clearService, new Gson());
//
//        // Setup routes
//        clearHandler.setupRoutes();
    }
}