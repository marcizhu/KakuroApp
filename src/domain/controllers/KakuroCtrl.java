package src.domain.controllers;

import src.domain.algorithms.Generator;
import src.domain.algorithms.Solver;
import src.domain.entities.*;
import src.repository.GameRepository;
import src.repository.KakuroRepository;
import src.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KakuroCtrl {
    KakuroRepository kakuroRepository;
    UserRepository userRepository;
    GameRepository gameRepository;

    public KakuroCtrl(KakuroRepository kakuroRepository, UserRepository userRepository, GameRepository gameRepository) {
        this.kakuroRepository = kakuroRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public Kakuro getKakuro(String name) throws Exception {
        return kakuroRepository.getKakuro(name);
    }

    public ArrayList<Map<String, Object>> getKakuroListByDifficulty(Difficulty difficulty, User user) throws Exception {
        ArrayList<Kakuro> kakuroList = kakuroRepository.getAllKakurosByDifficulty(difficulty);
        return computeResultFromKakuroList(kakuroList, user);
    }

    public ArrayList<Map<String, Object>> getKakuroListByUser(User user) throws Exception {
        ArrayList<Kakuro> kakuroList = kakuroRepository.getAllKakurosByUser(user);
        return computeResultFromKakuroList(kakuroList, user);
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

        Kakuro kakuro = new Kakuro(kakuroname, Difficulty.USER_MADE, board, user, "");
        saveKakuro(kakuro);

        return kakuro;
    }

    public Map<String, Object> saveKakuroFromGeneratorParameters(User user, int rows, int columns, Difficulty difficulty, boolean forceUnique, String kakuroname) throws Exception {
        Generator generator = new Generator(rows, columns, difficulty, forceUnique);
        long initTime = System.currentTimeMillis();
        generator.generate();
        long generatorTime = System.currentTimeMillis() - initTime;
        Board board = generator.getGeneratedBoard();

        Kakuro kakuro = new Kakuro(kakuroname, difficulty, board, user, generator.getEncodedSeed());
        saveKakuro(kakuro);

        Map<String, Object> result = new HashMap<>();
        result.put("board", board.toString());
        result.put("seed", kakuro.getSeed());
        result.put("generatorTime", generatorTime);

        return result;
    }

    public Map<String, Object> saveKakuroFromGeneratorSeed(User user, String seed, String kakuroname) throws Exception {
        String[] parameters = seed.split("_");
        int rows = Integer.parseInt(parameters[0]);
        int columns = Integer.parseInt(parameters[1]);
        Difficulty difficulty;
        switch (parameters[2]) {
            case "E": difficulty = Difficulty.EASY;    break;
            case "M": difficulty = Difficulty.MEDIUM;  break;
            case "H": difficulty = Difficulty.HARD;    break;
            case "X": difficulty = Difficulty.EXTREME; break;
            default: throw new Exception("Invalid seed, difficulty could not be decoded");
        }
        boolean forceUnique;
        switch (parameters[3]) {
            case "F": forceUnique = true;  break;
            case "N": forceUnique = false; break;
            default: throw new Exception("Invalid seed, force unique could not be decoded");
        }
        long seedValue = Long.parseLong(parameters[4]);

        Generator generator = new Generator(rows, columns, difficulty, seedValue, forceUnique);
        long initTime = System.currentTimeMillis();
        generator.generate();
        long generatorTime = System.currentTimeMillis() - initTime;
        Board board = generator.getGeneratedBoard();

        Kakuro kakuro = new Kakuro(kakuroname, difficulty, board, user, generator.getEncodedSeed());
        saveKakuro(kakuro);

        Map<String, Object> result = new HashMap<>();
        result.put("board", board.toString());
        result.put("seed", kakuro.getSeed());
        result.put("generatorTime", generatorTime);

        return result;
    }

    private ArrayList<Map<String, Object>> computeResultFromKakuroList(ArrayList<Kakuro> kakuroList, User user) throws IOException {
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        for (Kakuro kakuro : kakuroList) {
            ArrayList<Game> kakuroGames = gameRepository.getAllGamesInKakuro(kakuro);
            int timesPlayed = kakuroGames.size();
            float bestTime = -1;
            String state = "neutral";
            for (Game game : kakuroGames) {
                if (game instanceof GameFinished && ((GameFinished) game).isSurrendered() &&
                        (game.getTimeSpent() < bestTime || bestTime == -1)) bestTime = game.getTimeSpent();
                if (game.getPlayerName().equals(user.getName())) {
                    if (game instanceof GameInProgress) state = "unfinished";
                    else if (state.equals("neutral") && game instanceof GameFinished) {
                        state = ((GameFinished) game).isSurrendered() ? "surrendered" : "solved";
                    }
                }
            }

            HashMap<String, Object> kakuroData = new HashMap<>();
            kakuroData.put("board", kakuro.getBoard().toString());
            kakuroData.put("name", kakuro.getName());
            kakuroData.put("difficulty", kakuro.getDifficulty().toString());
            kakuroData.put("timesPlayed", timesPlayed);
            kakuroData.put("createdBy", kakuro.getCreatedBy() == null ? "System" : kakuro.getCreatedBy().getName());
            kakuroData.put("createdAt", kakuro.getCreatedAt());
            kakuroData.put("bestTime", (int)bestTime);
            kakuroData.put("state", state);
            result.add(kakuroData);
        }
        return result;
    }
}
