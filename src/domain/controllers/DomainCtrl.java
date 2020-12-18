package src.domain.controllers;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.*;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Map;

public class DomainCtrl {
    UserRepository userRepository;
    KakuroRepository kakuroRepository;
    UserCtrl userCtrl;
    KakuroCtrl kakuroCtrl;

    public DomainCtrl() {
        DB driver = new DB();
        userRepository = new UserRepositoryDB(driver);
        kakuroRepository = new KakuroRepositoryDB(driver);
        userCtrl = new UserCtrl(userRepository);
        kakuroCtrl = new KakuroCtrl(kakuroRepository, userRepository);
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

    // TODO: testing purposes, delete when it is properly implemented
    public GameplayCtrl newGameInstance(String sessionID, String kakuroID) {
        User user = new User(sessionID);
        Kakuro kakuro;
        try {
            kakuro = new Kakuro("Fix this!!", Difficulty.HARD, Reader.fromFile("data/kakuros/unsolved/cpu_burner.kak"));
            kakuroRepository.saveKakuro(kakuro);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

        return new GameplayCtrl(user, kakuro);
    }

    public KakuroCreationCtrl newCreatorInstance(String sessionID, int numRows, int numCols) {
        User user = new User(sessionID);
        return new KakuroCreationCtrl(user, numRows, numCols, kakuroCtrl);
    }
}
