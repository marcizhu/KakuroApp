package src;

import src.domain.algorithms.Generator;
import src.domain.entities.*;
import src.repository.*;

import java.io.IOException;
import java.util.ArrayList;

public class PopulateDB {
    public static void populateDB() throws IOException {
        cleanDB();

        createUser("Cesc");
        createUser("Alex");
        createUser("Marc");
        createUser("Xabi");

        createKakuro("Easy 5x5", Difficulty.EASY, 5, 5);
        createKakuro("Easy 10x10", Difficulty.EASY, 10, 10);
        createKakuro("Easy 15x15", Difficulty.EASY, 15, 15);
        createKakuro("Easy 20x20", Difficulty.EASY, 20, 20);

        createKakuro("Medium 5x5", Difficulty.MEDIUM, 5, 5);
        createKakuro("Medium 10x10", Difficulty.MEDIUM, 10, 10);
        createKakuro("Medium 15x15", Difficulty.MEDIUM, 15, 15);
        createKakuro("Medium 20x20", Difficulty.MEDIUM, 20, 20);

        createKakuro("Hard 5x5", Difficulty.HARD, 5, 5);
        createKakuro("Hard 10x10", Difficulty.HARD, 10, 10);
        createKakuro("Hard 15x15", Difficulty.HARD, 15, 15);
        createKakuro("Hard 20x20", Difficulty.HARD, 20, 20);

        createKakuro("Extreme 5x5", Difficulty.EXTREME, 5, 5);
        createKakuro("Extreme 10x10", Difficulty.EXTREME, 10, 10);
        createKakuro("Extreme 15x15", Difficulty.EXTREME, 15, 15);
        createKakuro("Extreme 20x20", Difficulty.EXTREME, 20, 20);
    }

    public static void createKakuro(String name, Difficulty diff, int width, int height) throws IOException{
        Generator gen = new Generator(width, height, diff);
        gen.generate();
        Board board = gen.getGeneratedBoard();
        Kakuro kak = new Kakuro(name, diff, board, null, "");

        KakuroRepository kr = new KakuroRepositoryDB(new DB());
        kr.saveKakuro(kak);
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
