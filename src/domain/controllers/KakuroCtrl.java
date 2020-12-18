package src.domain.controllers;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.KakuroRepository;
import src.repository.UserRepository;

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

    public ArrayList<Map<String, Object>> getKakuroListByDifficulty(Difficulty difficulty) throws Exception {
        ArrayList<Kakuro> kakuroList = kakuroRepository.getAllKakurosByDifficulty(difficulty);
        return computeResultFromKakuroList(kakuroList);
    }

    public ArrayList<Map<String, Object>> getKakuroListByUser(String username) throws Exception {
        User user = userRepository.getUser(username);
        if (user == null) {
            throw new Exception("User not found"); // TODO: maybe create custom exception obejcts for each case and use them?
        }

        ArrayList<Kakuro> kakuroList = kakuroRepository.getAllKakurosByUser(user);
        return computeResultFromKakuroList(kakuroList);
    }

    // returns whether kakuro instance could be saved to database.
    public boolean saveKakuro(Kakuro kakuro) throws Exception {
        User user = userRepository.getUser(kakuro.getUser().getName());
        if (user == null) {
            throw new Exception("User not found"); // TODO: maybe create custom exception obejcts for each case and use them?
        }

        Kakuro kak = kakuroRepository.getKakuro(kakuro.getName());
        if (kak != null) return false;

        kakuroRepository.saveKakuro(kakuro);
        return true;
    }

    private ArrayList<Map<String, Object>> computeResultFromKakuroList(ArrayList<Kakuro> kakuroList) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        for (Kakuro kakuro : kakuroList) {
            HashMap<String, Object> kakuroData = new HashMap<>();
            kakuroData.put("board", kakuro.getBoard().toString());
            kakuroData.put("name", kakuro.getName());
            kakuroData.put("difficulty", kakuro.getDifficulty().toString());
            kakuroData.put("timesPlayed", 12); // TODO: compute this
            kakuroData.put("createdBy", kakuro.getCreatedBy().getName());
            kakuroData.put("createdAt", kakuro.getCreatedAt());
            kakuroData.put("bestTime", 14400); // TODO: compute this (given in seconds?)
            kakuroData.put("state", "unfinished"); // TODO: compute this
            result.add(kakuroData);
        }
        return result;
    }
}
