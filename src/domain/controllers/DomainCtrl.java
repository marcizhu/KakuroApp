package src.domain.controllers;

import src.domain.entities.Difficulty;
import src.domain.entities.Game;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
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

    public Pair<GameplayCtrl, String> newGameInstance(String username, String kakuroname) {
        try {
            User user = userCtrl.getUser(username);
            Game game = gameCtrl.getGameInProgress(username, kakuroname);
            Kakuro kakuro;
            if (game != null) kakuro = game.getKakuro();
            else kakuro = kakuroCtrl.getKakuro(kakuroname);
            return new Pair<>(new GameplayCtrl(user, kakuro, gameCtrl), null);
        } catch (Exception e) {
            return new Pair<>(null, e.getMessage());
        }
    }

    public KakuroCreationCtrl newCreatorInstance(String sessionID, int numRows, int numCols) {
        User user = new User(sessionID);
        return new KakuroCreationCtrl(user, numRows, numCols, kakuroCtrl);
    }
}
