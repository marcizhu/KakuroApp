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

    private ArrayList<Map<String, Object>> computeResultFromKakuroList(ArrayList<Kakuro> kakuroList) {
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        // TODO: unhardcode this
        String board = "";
        try {
            board = Reader.fromFile("data/kakuros/solved/jutge.kak").toString();
        } catch (Exception e) {
            System.out.println(e);
        }

        for (Kakuro kakuro : kakuroList) {
            HashMap<String, Object> kakuroData = new HashMap<>();
            kakuroData.put("board", board); // TODO: unhardcode this
            kakuroData.put("name", "Breakfast Kakuro"); // TODO: unhardcode this
            kakuroData.put("difficulty", kakuro.getDifficulty().toString());
            kakuroData.put("timesPlayed", 12); // TODO: compute this
            kakuroData.put("createdBy", kakuro.getCreatedBy());
            kakuroData.put("createdAt", kakuro.getCreatedAt());
            kakuroData.put("bestTime", 14400); // TODO: compute this (given in seconds?)
            kakuroData.put("state", "unfinished"); // TODO: compute this
            result.add(kakuroData);
        }
        return result;
    }
}
