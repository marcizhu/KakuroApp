package src.domain.controllers;

import src.domain.entities.*;
import src.repository.GameRepository;
import src.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RankingCtrl {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public RankingCtrl(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public ArrayList<Map<String, Object>> getRankingByPoints() throws Exception {
        ArrayList<User> users = userRepository.getAllUsers();
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> map = new HashMap<>();

            float easy = 0, medium = 0, hard = 0, extreme = 0;
            ArrayList<Game> kakuros = gameRepository.getAllGamesByUser(user.getName());
            for (Game game : kakuros) {
                if (game instanceof GameInProgress) continue;
                if (((GameFinished)game).isSurrendered()) continue;

                /**/ if (game.getKakuro().getDifficulty() == Difficulty.EASY)    easy    += ((GameFinished)game).getScore();
                else if (game.getKakuro().getDifficulty() == Difficulty.MEDIUM)  medium  += ((GameFinished)game).getScore();
                else if (game.getKakuro().getDifficulty() == Difficulty.HARD)    hard    += ((GameFinished)game).getScore();
                else if (game.getKakuro().getDifficulty() == Difficulty.EXTREME) extreme += ((GameFinished)game).getScore();
            }

            map.put("name", user.getName());
            map.put("easyPts", easy);
            map.put("mediumPts", medium);
            map.put("hardPts", hard);
            map.put("extremePts", extreme);
            map.put("totalPts", easy + medium + hard + extreme);

            result.add(map);
        }

        result.sort((o1, o2) -> Float.compare((float)o2.get("totalPts"), (float)o1.get("totalPts")));
        return result;
    }

    public ArrayList<Map<String, Object>> getRankingByGamesPlayed() throws Exception {
        ArrayList<User> users = userRepository.getAllUsers();
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> map = new HashMap<>();

            float easy = 0, medium = 0, hard = 0, extreme = 0;
            ArrayList<Game> kakuros = gameRepository.getAllGamesByUser(user.getName());
            for (Game game : kakuros) {
                if (game instanceof GameInProgress) continue;

                /**/ if (game.getKakuro().getDifficulty() == Difficulty.EASY)    easy++;
                else if (game.getKakuro().getDifficulty() == Difficulty.MEDIUM)  medium++;
                else if (game.getKakuro().getDifficulty() == Difficulty.HARD)    hard++;
                else if (game.getKakuro().getDifficulty() == Difficulty.EXTREME) extreme++;
            }

            map.put("name", user.getName());
            map.put("easyGames", easy);
            map.put("mediumGames", medium);
            map.put("hardGames", hard);
            map.put("extremeGames", extreme);
            map.put("totalGames", easy + medium + hard + extreme);

            result.add(map);
        }

        result.sort((o1, o2) -> Float.compare((float)o2.get("totalGames"), (float)o1.get("totalGames")));
        return result;
    }

    public ArrayList<Map<String, Object>> getRankingByTimeInDifficulty(String difficulty) throws Exception {
        ArrayList<User> users = userRepository.getAllUsers();
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> map = new HashMap<>();

            float totalTime = 0.0f;
            int totalGames = 0;

            ArrayList<Game> kakuros = gameRepository.getAllGamesByUser(user.getName());
            for (Game game : kakuros) {
                if (game instanceof GameInProgress) continue;
                if (((GameFinished)game).isSurrendered()) continue;
                if (!game.getKakuro().getDifficulty().toString().equals(difficulty)) continue;

                totalTime += game.getTimeSpent();
                totalGames++;
            }

            map.put("name", user.getName());
            map.put("avgTime", totalTime / (float)totalGames);

            result.add(map);
        }

        result.sort((o1, o2) -> Float.compare((float)o1.get("avgTime"), (float)o2.get("avgTime")));
        return result;
    }
}
