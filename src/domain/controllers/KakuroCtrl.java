package src.domain.controllers;

import src.domain.algorithms.Generator;
import src.domain.algorithms.Solver;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.KakuroRepository;
import src.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KakuroCtrl {
    KakuroRepository kakuroRepository;
    UserRepository userRepository;

    public KakuroCtrl(KakuroRepository kakuroRepository, UserRepository userRepository) {
        this.kakuroRepository = kakuroRepository;
        this.userRepository = userRepository;
    }

    public Kakuro getKakuro(String name) throws Exception {
        return kakuroRepository.getKakuro(name);
    }

    public ArrayList<Map<String, Object>> getKakuroListByDifficulty(Difficulty difficulty) throws Exception {
        ArrayList<Kakuro> kakuroList = kakuroRepository.getAllKakurosByDifficulty(difficulty);
        return computeResultFromKakuroList(kakuroList);
    }

    public ArrayList<Map<String, Object>> getKakuroListByUser(User user) throws Exception {
        ArrayList<Kakuro> kakuroList = kakuroRepository.getAllKakurosByUser(user);
        return computeResultFromKakuroList(kakuroList);
    }

    public void saveKakuro(Kakuro kakuro) throws Exception {
        Kakuro kak = kakuroRepository.getKakuro(kakuro.getName());
        if (kak != null) throw new Exception("The provided name is already used by a Kakuro in the database");
        kakuroRepository.saveKakuro(kakuro);
    }

    public Kakuro saveKakuroFromFile(User user, String filePath, String kakuroname) throws Exception {
        Board board = Reader.fromFile(filePath);
        Solver solver = new Solver(board);
        solver.solve();
        if (solver.getSolutions().size() <= 0) throw new Exception("The provided Kakuro has no solution");

        Kakuro kakuro = new Kakuro(kakuroname, Difficulty.USER_MADE, board, user);
        saveKakuro(kakuro);

        return kakuro;
    }

    public Map<String, Object> saveKakuroFromGeneratorParameters(User user, int rows, int columns, Difficulty difficulty, boolean forceUnique, String kakuroname) throws Exception {
        Generator generator = new Generator(rows, columns, difficulty, forceUnique);
        long initTime = System.currentTimeMillis();
        generator.generate();
        long generatorTime = System.currentTimeMillis() - initTime;
        Board board = generator.getGeneratedBoard();

        Kakuro kakuro = new Kakuro(kakuroname, Difficulty.USER_MADE, board, user);
        saveKakuro(kakuro);

        Map<String, Object> result = new HashMap<>();
        result.put("board", board.toString());
        result.put("generatorTime", generatorTime);

        return result;
    }

    public Map<String, Object> saveKakuroFromGeneratorSeed(User user, String encodedSeed, String kakuroname) throws Exception {
        int rows = 10; // TODO: compute this from encodedSeed
        int columns = 10; // TODO: compute this from encodedSeed
        Difficulty difficulty = Difficulty.MEDIUM; // TODO: compute this from encodedSeed
        long seed = 1234567890; // TODO: compute this from encodedSeed

        Generator generator = new Generator(rows, columns, difficulty, seed);
        long initTime = System.currentTimeMillis();
        generator.generate();
        long generatorTime = System.currentTimeMillis() - initTime;
        Board board = generator.getGeneratedBoard();

        Kakuro kakuro = new Kakuro(kakuroname, Difficulty.USER_MADE, board, user);
        saveKakuro(kakuro);

        Map<String, Object> result = new HashMap<>();
        result.put("board", board.toString());
        result.put("generatorTime", generatorTime);

        return result;
    }

    private ArrayList<Map<String, Object>> computeResultFromKakuroList(ArrayList<Kakuro> kakuroList) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        for (Kakuro kakuro : kakuroList) {
            HashMap<String, Object> kakuroData = new HashMap<>();
            kakuroData.put("board", kakuro.getBoard().toString());
            kakuroData.put("name", kakuro.getName());
            kakuroData.put("difficulty", kakuro.getDifficulty().toString());
            kakuroData.put("timesPlayed", 12); // TODO: compute this
            kakuroData.put("createdBy", kakuro.getCreatedBy() == null ? "System" : kakuro.getCreatedBy().getName());
            kakuroData.put("createdAt", kakuro.getCreatedAt());
            kakuroData.put("bestTime", 14400); // TODO: compute this (given in seconds?)
            kakuroData.put("state", "unfinished"); // TODO: compute this
            result.add(kakuroData);
        }
        return result;
    }
}
