package src.domain.controllers;

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
        kakuroRepository.saveKakuro(kakuro);

        return kakuro;
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
