package src.domain.controllers;

import src.domain.entities.*;
import src.repository.*;
import src.utils.Pair;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.*;

public class DomainCtrl {
    UserRepository userRepository;
    KakuroRepository kakuroRepository;
    GameRepository gameRepository;

    UserCtrl userCtrl;
    KakuroCtrl kakuroCtrl;
    GameCtrl gameCtrl;
    RankingCtrl rankingCtrl;

    public DomainCtrl() {
        DB driver = new DB();

        userRepository = new UserRepositoryDB(driver);
        kakuroRepository = new KakuroRepositoryDB(driver);
        gameRepository = new GameRepositoryDB(driver);

        userCtrl = new UserCtrl(userRepository);
        kakuroCtrl = new KakuroCtrl(kakuroRepository, userRepository, gameRepository);
        gameCtrl = new GameCtrl(gameRepository, userRepository);
        rankingCtrl = new RankingCtrl(gameRepository, userRepository);
    }

    public Pair<ArrayList<String>, String> getUserList() {
        try {
            ArrayList<String> userList = userCtrl.getUserList();
            return new Pair<>(userList, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<Boolean, String> loginUser(String username) {
        try {
            boolean userExists = userCtrl.loginUser(username);
            if (!userExists) return new Pair<>(false, "Invalid user");
            return new Pair<>(true, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<Boolean, String> registerUser(String username) {
        try {
            boolean result = userCtrl.registerUser(username);
            if (!result) return new Pair<>(false, "User already exists");
            return new Pair<>(true, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<ArrayList<Map<String, Object>>, String> getKakuroListByUser(String username) {
        ArrayList<Map<String, Object>> result;
        try {
            User user = userCtrl.getUser(username);
            result = kakuroCtrl.getKakuroListByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(null, e.getMessage());
        }
        return new Pair<>(result, null);
    }

    public Pair<ArrayList<Map<String, Object>>, String> getKakuroListByDifficulty(String difficulty, String username) {
        ArrayList<Map<String, Object>> result;
        try {
            User user = userCtrl.getUser(username);
            result = kakuroCtrl.getKakuroListByDifficulty(Difficulty.valueOf(difficulty), user);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
        return new Pair<>(result, null);
    }

    public Pair<Map<String, Integer>, String> getNumberOfGamesPlayed(String username) {
        // TODO: alex, all difficulties
        return null;
    }

    public Pair<ArrayList<Map<String, Object>>, String> getGameHistory(String username) {
        ArrayList<Map<String, Object>> result;
        try {
            result = gameCtrl.getGameHistory(username);
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(null, e.getMessage());
        }
        return new Pair<>(result, null);
    }

    public Pair<ArrayList<Map<String, Object>>, String> getRankingByPoints() {
        ArrayList<Map<String, Object>> result;
        try {
            result = rankingCtrl.getRankingByPoints();
            return new Pair<>(result, null);
        } catch(Exception e) {
            return new Pair<>(new ArrayList<>(), e.getMessage());
        }
    }

    public Pair<ArrayList<Map<String, Object>>, String> getRankingByGamesPlayed() {
        ArrayList<Map<String, Object>> result;
        try {
            result = rankingCtrl.getRankingByGamesPlayed();
            return new Pair<>(result, null);
        } catch(Exception e) {
            return new Pair<>(new ArrayList<>(), e.getMessage());
        }
    }

    public Pair<ArrayList<Map<String, Object>>, String> getRankingByTimeInDifficulty(String difficulty) {
        ArrayList<Map<String, Object>> result;
        try {
            result = rankingCtrl.getRankingByTimeInDifficulty(difficulty);
            return new Pair<>(result, null);
        } catch(Exception e) {
            return new Pair<>(new ArrayList<>(), e.getMessage());
        }
    }

    public Pair<GameplayCtrl, String> newGameInstance(String username, String kakuroname) {
        try {
            GameInProgress game = gameCtrl.getGameInProgress(username, kakuroname);
            if (game != null) return new Pair<>(new GameplayCtrl(game, gameCtrl), null);
            User user = userCtrl.getUser(username);
            Kakuro kakuro = kakuroCtrl.getKakuro(kakuroname);
            return new Pair<>(new GameplayCtrl(user, kakuro, gameCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<GameplayCtrl, String> newImportedGameInstance(String username, String filePath, String kakuroname) {
        try {
            User user = userCtrl.getUser(username);
            Kakuro kakuro = kakuroCtrl.saveKakuroFromFile(user, filePath, kakuroname);
            return new Pair<>(new GameplayCtrl(user, kakuro, gameCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<KakuroCreationCtrl, String> newCreatorInstance(String username, int numRows, int numCols) {
        try {
            User user = userCtrl.getUser(username);
            return new Pair<>(new KakuroCreationCtrl(user, numRows, numCols, kakuroCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<KakuroCreationCtrl, String> newImportedCreatorInstance(String username, String filePath) {
        try {
            User user = userCtrl.getUser(username);
            Board board = Reader.fromFile(filePath);
            return new Pair<>(new KakuroCreationCtrl(user, board, kakuroCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<Map<String, Object>, String> generateKakuroFromParameters(String username, int rows, int columns, String difficulty, boolean forceUnique, String kakuroName) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Object> result = kakuroCtrl.saveKakuroFromGeneratorParameters(user, rows, columns, Difficulty.valueOf(difficulty), forceUnique, kakuroName);
            return new Pair<>(result, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<Map<String, Object>, String> generateKakuroFromSeed(String username, String seed, String kakuroName) {
        try {
            User user = userCtrl.getUser(username);
            Map<String, Object> result = kakuroCtrl.saveKakuroFromGeneratorSeed(user, seed, kakuroName);
            return new Pair<>(result, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public Pair<Boolean, String> exportKakuro(String kakuroName, String filePath) {
        try {
            Kakuro kakuro = kakuroCtrl.getKakuro(kakuroName);
            FileWriter myWriter = new FileWriter(filePath+".txt");
            myWriter.write(kakuro.getBoard().toString() + "\n");
            myWriter.close();
            return new Pair<>(true, null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }
}
