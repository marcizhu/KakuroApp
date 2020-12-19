package src.domain.controllers;

import src.domain.entities.*;
import src.repository.*;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class DomainCtrl {
    UserRepository userRepository;
    KakuroRepository kakuroRepository;
    GameRepository gameRepository;

    UserCtrl userCtrl;
    KakuroCtrl kakuroCtrl;
    GameCtrl gameCtrl;

    public DomainCtrl() {
        DB driver = new DB();

        userRepository = new UserRepositoryDB(driver);
        kakuroRepository = new KakuroRepositoryDB(driver);
        gameRepository = new GameRepositoryDB(driver);

        userCtrl = new UserCtrl(userRepository);
        kakuroCtrl = new KakuroCtrl(kakuroRepository, userRepository);
        gameCtrl = new GameCtrl(gameRepository, userRepository);
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
            result = kakuroCtrl.getKakuroListByUser(username);
        } catch (Exception e) {
            e.printStackTrace();
            return new Pair<>(null, e.getMessage());
        }
        return new Pair<>(result, null);
    }

    public Pair<ArrayList<Map<String, Object>>, String> getKakuroListByDifficulty(String difficulty) {
        ArrayList<Map<String, Object>> result;
        try {
            result = kakuroCtrl.getKakuroListByDifficulty(Difficulty.valueOf(difficulty));
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
        return new Pair<>(result, null);
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

    public Pair<GameplayCtrl, String> newImportedGameInstance(String username, String filePath) {
        // TODO: validate kakuro (and the format), if valid register it in data base as USER_MADE and return new game instance
        return new Pair<>(null, "Functionality currently not implemented");
    }

    public KakuroCreationCtrl newCreatorInstance(String username, int numRows, int numCols) {
        User user = new User(username);
        return new KakuroCreationCtrl(user, numRows, numCols, kakuroCtrl);
    }

    public Pair<KakuroCreationCtrl, String> newImportedCreatorInstance(String username, String filePath) {
        // TODO: validate kakuro format, extract the number of rows and columns and return a creation ctrl
        //  using constructor
        // new KakuroCreationCtrl(user, initialBoard, kakuroCtrl);
        return new Pair<>(null, "Functionality currently not implemented");
    }

    public void generateKakuroFromParameters(int rows, int columns, String difficulty, boolean forceUnique) {
        // TODO: this shouldn't be void, it should generate the kakuro, save it to the database and return
        //  the board.toSting(), the time that the generator spent generating the kakuro (like in the example), etc.

        // long initTime = System.currentTimeMillis();
        // generator.generate();
        // timeToReturn = System.currentTimeMillis() - initTime;
    }

    public void generateKakuroFromSeed(String seed) {
        // TODO: this shouldn't be void, it should decode the seed to extract rows, columns, difficulty, forceUnique and long seed
        //  generate the kakuro, save it to the database and return
        //  the board.toSting(), the time that the generator spent generating the kakuro (like in the example), etc.

        // long initTime = System.currentTimeMillis();
        // generator.generate();
        // timeToReturn = System.currentTimeMillis() - initTime;
    }
}
