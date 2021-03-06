package src.domain.controllers;

import src.domain.algorithms.Generator;
import src.domain.algorithms.Solver;
import src.domain.entities.*;
import src.presentation.utils.RGBUtils;
import src.repository.GameRepository;
import src.repository.KakuroRepository;
import src.repository.UserRepository;

import java.io.IOException;
import java.util.*;

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

    private ArrayList<Map<String, Object>> computeResultFromKakuroList(ArrayList<Kakuro> kakuroList, User user) throws IOException {
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        Collections.sort(kakuroList, new Comparator<Kakuro>() {
            @Override
            public int compare(Kakuro a, Kakuro b) {
                if (a.getCreatedAt().getTime() == b.getCreatedAt().getTime()) return 0;
                if (a.getCreatedAt().getTime() < b.getCreatedAt().getTime()) return 1;
                return -1;
            }
        });

        for (Kakuro kakuro : kakuroList) {
            ArrayList<Game> kakuroGames = gameRepository.getAllGamesInKakuro(kakuro);
            int timesPlayed = kakuroGames.size();
            float bestTime = -1;
            String state = "neutral";
            for (Game game : kakuroGames) {
                if (game instanceof GameFinished && !((GameFinished) game).isSurrendered() &&
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
            kakuroData.put("seed", kakuro.getSeed());
            kakuroData.put("color", kakuro.getColorCode());
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

    public void validateKakuroName(String kakuroname) throws Exception {
        if (kakuroname.equals("")) throw new Exception("The name of the Kakuro cannot be null");
        Kakuro existentKakuro = kakuroRepository.getKakuro(kakuroname);
        if (existentKakuro != null) throw new Exception("The provided name is already used by a Kakuro in the database");
    }

    public void saveKakuro(Kakuro kakuro) throws Exception {
        validateKakuroName(kakuro.getName());

        kakuroRepository.saveKakuro(kakuro);
    }

    public Kakuro saveKakuroFromFile(User user, String filePath, String kakuroname) throws Exception {
        validateKakuroName(kakuroname);

        Board board = Reader.fromFile(filePath);
        Solver solver = new Solver(board);
        solver.solve();
        if (solver.getSolutions().size() <= 0) throw new Exception("The provided Kakuro has no solution");

        Kakuro kakuro = new Kakuro(kakuroname, Difficulty.USER_MADE, board, user, "", RGBUtils.rndColorCode(board.hashCode()));
        kakuroRepository.saveKakuro(kakuro);

        return kakuro;
    }

    public Map<String, Object> saveKakuroFromGeneratorParameters(User user, int rows, int columns, Difficulty difficulty, boolean forceUnique, String kakuroname) throws Exception {
        validateKakuroName(kakuroname);

        Generator generator = new Generator(rows, columns, difficulty, forceUnique);
        long initTime = System.currentTimeMillis();
        generator.generate();
        long generatorTime = System.currentTimeMillis() - initTime;
        Board board = generator.getGeneratedBoard();

        String[] parameters = generator.getEncodedSeed().split("_");
        long seedValue = Long.parseLong(parameters[4]);

        Kakuro kakuro = new Kakuro(kakuroname, difficulty, board, user, generator.getEncodedSeed(), RGBUtils.rndColorCode(seedValue));
        kakuroRepository.saveKakuro(kakuro);

        Map<String, Object> result = new HashMap<>();
        result.put("board", board.toString());
        result.put("color", kakuro.getColorCode());
        result.put("seed", kakuro.getSeed());
        result.put("generatorTime", generatorTime);

        return result;
    }

    public Map<String, Object> saveKakuroFromGeneratorSeed(User user, String seed, String kakuroname) throws Exception {
        validateKakuroName(kakuroname);

        String[] parameters = seed.split("_");
        int rows, columns;
        try {
            rows = Integer.parseInt(parameters[0]);
            columns = Integer.parseInt(parameters[1]);
        } catch (Exception e) {
            throw new Exception("seed_format");
        }
        if (rows < 3 || rows > 75) throw new Exception("The number of rows should be between 3 and 75");
        if (columns < 3 || columns > 75) throw new Exception("The number of columns should be between 3 and 75");
        Difficulty difficulty;
        switch (parameters[2]) {
            case "E": difficulty = Difficulty.EASY;    break;
            case "M": difficulty = Difficulty.MEDIUM;  break;
            case "H": difficulty = Difficulty.HARD;    break;
            case "X": difficulty = Difficulty.EXTREME; break;
            default: throw new Exception("seed_format");
        }
        boolean forceUnique;
        switch (parameters[3]) {
            case "F": forceUnique = true;  break;
            case "N": forceUnique = false; break;
            default: throw new Exception("seed_format");
        }
        long seedValue;
        try {
            seedValue = Long.parseLong(parameters[4]);
        } catch (Exception e) {
            throw new Exception("seed_format");
        }

        Generator generator = new Generator(rows, columns, difficulty, seedValue, forceUnique);
        long initTime = System.currentTimeMillis();
        generator.generate();
        long generatorTime = System.currentTimeMillis() - initTime;
        Board board = generator.getGeneratedBoard();

        Kakuro kakuro = new Kakuro(kakuroname, difficulty, board, user, generator.getEncodedSeed(), RGBUtils.rndColorCode(seedValue));
        kakuroRepository.saveKakuro(kakuro);

        Map<String, Object> result = new HashMap<>();
        result.put("board", board.toString());
        result.put("color", kakuro.getColorCode());
        result.put("seed", kakuro.getSeed());
        result.put("generatorTime", generatorTime);

        return result;
    }
}
