package src;

import src.domain.algorithms.Generator;
import src.domain.controllers.KakuroCtrl;
import src.domain.entities.*;
import src.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class PopulateDB {
    public static void populateDB() throws Exception {
        cleanDB();

        createUser("Cesc");
        createUser("Alex");
        createUser("Marc");
        createUser("Xavi");

        createKakuro("Easy 8x8 1", Difficulty.EASY, 8, 8);
        createKakuro("Easy 8x8 2", Difficulty.EASY, 8, 8);
        createKakuro("Easy 8x8 3", Difficulty.EASY, 8, 8);
        createKakuro("Easy 10x10 1", Difficulty.EASY, 10, 10);
        createKakuro("Easy 10x10 2", Difficulty.EASY, 10, 10);
        createKakuro("Easy 10x10 3", Difficulty.EASY, 10, 10);
        createKakuro("Easy 13x13 1", Difficulty.EASY, 13, 13);
        createKakuro("Easy 13x13 2", Difficulty.EASY, 13, 13);
        createKakuro("Easy 13x13 3", Difficulty.EASY, 13, 13);


        createKakuro("Medium 8x8 1", Difficulty.MEDIUM, 8, 8);
        createKakuro("Medium 8x8 2", Difficulty.MEDIUM, 8, 8);
        createKakuro("Medium 8x8 3", Difficulty.MEDIUM, 8, 8);
        createKakuro("Medium 10x10 1", Difficulty.MEDIUM, 10, 10);
        createKakuro("Medium 10x10 2", Difficulty.MEDIUM, 10, 10);
        createKakuro("Medium 10x10 3", Difficulty.MEDIUM, 10, 10);
        createKakuro("Medium 13x13 1", Difficulty.MEDIUM, 13, 13);
        createKakuro("Medium 13x13 2", Difficulty.MEDIUM, 13, 13);
        createKakuro("Medium 13x13 3", Difficulty.MEDIUM, 13, 13);

        createKakuro("Hard 8x8 1", Difficulty.HARD, 8, 8);
        createKakuro("Hard 8x8 2", Difficulty.HARD, 8, 8);
        createKakuro("Hard 8x8 3", Difficulty.HARD, 8, 8);
        createKakuro("Hard 10x10 1", Difficulty.HARD, 10, 10);
        createKakuro("Hard 10x10 2", Difficulty.HARD, 10, 10);
        createKakuro("Hard 10x10 3", Difficulty.HARD, 10, 10);
        createKakuro("Hard 13x13 1", Difficulty.HARD, 13, 13);
        createKakuro("Hard 13x13 2", Difficulty.HARD, 13, 13);
        createKakuro("Hard 13x13 3", Difficulty.HARD, 13, 13);

        createKakuro("Extreme 8x8 1", Difficulty.EXTREME, 8, 8);
        createKakuro("Extreme 8x8 2", Difficulty.EXTREME, 8, 8);
        createKakuro("Extreme 8x8 3", Difficulty.EXTREME, 8, 8);
        createKakuro("Extreme 10x10 1", Difficulty.EXTREME, 10, 10);
        createKakuro("Extreme 10x10 2", Difficulty.EXTREME, 10, 10);
        createKakuro("Extreme 10x10 3", Difficulty.EXTREME, 10, 10);
        createKakuro("Extreme 13x13 1", Difficulty.EXTREME, 13, 13);
        createKakuro("Extreme 13x13 2", Difficulty.EXTREME, 13, 13);
        createKakuro("Extreme 13x13 3", Difficulty.EXTREME, 13, 13);
    }

    public static void createKakuro(String name, Difficulty diff, int width, int height) throws Exception {
        KakuroCtrl kc = new KakuroCtrl(new KakuroRepositoryDB(new DB()), new UserRepositoryDB(new DB()), new GameRepositoryDB(new DB()));
        Map<String, Object> m = kc.saveKakuroFromGeneratorParameters(null, height, width, diff, true, name);
    }

    public static void createUser (String name) throws IOException {
        UserRepository ur = new UserRepositoryDB(new DB());
        User u = new User(name);
        ur.saveUser(u);
    }

    public static void cleanDB() throws IOException {
        deleteAllUsers();
        deleteAllKakuros();
        deleteAllGames();
        deleteAllBoards();
    }

    public static void deleteAllUsers() throws IOException {
        UserRepository ur = new UserRepositoryDB(new DB());
        ArrayList<User> users = ur.getAllUsers();
        for (User u : users) ur.deleteUser(u);
    }

    public static void deleteAllKakuros() throws IOException {
        KakuroRepository kr = new KakuroRepositoryDB(new DB());
        ArrayList<Kakuro> kakuros = kr.getAllKakuros();
        for (Kakuro k : kakuros) kr.deleteKakuro(k);
    }

    public static void deleteAllGames() throws IOException {
        GameRepository gr = new GameRepositoryDB(new DB());
        ArrayList<Game> games = gr.getAllGames();
        for (Game g : games) gr.deleteGame(g);
    }

    public static void deleteAllBoards() throws IOException {
        BoardRepository br = new BoardRepositoryDB(new DB());
        ArrayList<Board> boards = br.getAllBoards();
        for (Board b : boards) br.deleteBoard(b);
    }
}
