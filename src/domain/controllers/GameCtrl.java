package src.domain.controllers;

import src.domain.entities.*;
import src.repository.GameRepository;
import src.repository.UserRepository;

import java.util.*;

public class GameCtrl {
    GameRepository gameRepository;
    UserRepository userRepository;

    public GameCtrl(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public GameInProgress getGameInProgress(String username, String kakuroname) throws Exception {
        ArrayList<Game> userGames = gameRepository.getAllGamesByUser(username);
        for (Game game : userGames) {
            if (game.getKakuroName().equals(kakuroname) && game instanceof GameInProgress) {
                return (GameInProgress) game;
            }
        }
        return null;
    }

    public ArrayList<Map<String, Object>> getGameHistory(String username) throws Exception {
        ArrayList<Map<String, Object>> result = new ArrayList<>();
        ArrayList<Game> userGames = gameRepository.getAllGamesByUser(username);
        Collections.sort(userGames, new Comparator<Game>() {
            @Override
            public int compare(Game a, Game b) {
                boolean aFinished = a instanceof GameFinished;
                boolean bFinished = b instanceof GameFinished;
                if (aFinished && !bFinished) return 1;
                else if (!aFinished && bFinished) return -1;
                else if (aFinished && bFinished) {
                    return ((GameFinished) b).getTimeFinished().compareTo(((GameFinished) a).getTimeFinished());
                } else { // !aFinished && !bFinished
                    return ((GameInProgress) b).getLastPlayed().compareTo(((GameInProgress) a).getLastPlayed());
                }
            }
        });
        for (Game game : userGames) {
            HashMap<String, Object> gameData = new HashMap<>();
            Kakuro kakuro = game.getKakuro();
            gameData.put("board", kakuro.getBoard().toString());
            gameData.put("name", kakuro.getName());
            gameData.put("width", kakuro.getBoard().getWidth());
            gameData.put("height", kakuro.getBoard().getHeight());
            gameData.put("difficulty", kakuro.getDifficulty().toString());
            gameData.put("timeSpent", (int)game.getTimeSpent());
            gameData.put("lastPlayed", (game instanceof GameFinished) ? ((GameFinished)game).getTimeFinished() : ((GameInProgress)game).getLastPlayed());
            if (game instanceof GameInProgress) {
                gameData.put("state", "unfinished");
            } else { // game instanceof GameFinished
                gameData.put("score", ((GameFinished)game).getScore());
                gameData.put("state", "finished");
            }
            result.add(gameData);
        }
        return result;
    }

    public Map<String, Integer> getNumberOfGamesPlayed(User user) throws Exception {
        ArrayList<Game> games = gameRepository.getAllGamesByUser(user);
        int easy = 0, medium = 0, hard = 0, extreme = 0, userMade = 0;
        for (Game game : games) {
            switch (game.getKakuro().getDifficulty()) {
                case EASY:      easy++;     break;
                case MEDIUM:    medium++;   break;
                case HARD:      hard++;     break;
                case EXTREME:   extreme++;  break;
                case USER_MADE: userMade++; break;
                default: break; // this should not happen
            }
        }

        HashMap<String, Integer> result = new HashMap<>();
        result.put("easy", easy);
        result.put("medium", medium);
        result.put("hard", hard);
        result.put("extreme", extreme);
        result.put("userMade", userMade);

        return result;
    }

    public Map<String, Object> getTopPointer(User user) throws Exception {
        ArrayList<Game> games = gameRepository.getAllGamesByUser(user);
        return getTopPointerFromGameList(games);
    }

    public Map<String, Object> getTopPointerInDifficulty(User user, Difficulty difficulty) throws Exception {
        ArrayList<Game> games = gameRepository.getAllGamesByDifficultyAndUser(difficulty, user);
        return getTopPointerFromGameList(games);
    }

    private Map<String, Object> getTopPointerFromGameList(ArrayList<Game> games) {
        Game topPointer = null;
        for (Game game : games) {
            if (game instanceof GameFinished && (topPointer == null || ((GameFinished) game).getScore() > ((GameFinished)topPointer).getScore())) {
                topPointer = game;
            }
        }
        Map<String, Object> result = new HashMap<>();
        if (topPointer == null) return result;
        Kakuro topKakuro = topPointer.getKakuro();
        result.put("board", topKakuro.getBoard().toString());
        result.put("name", topKakuro.getName());
        result.put("width", topKakuro.getBoard().getWidth());
        result.put("height", topKakuro.getBoard().getHeight());
        result.put("difficulty", topKakuro.getDifficulty().toString());
        result.put("timeSpent", (int)topPointer.getTimeSpent());
        result.put("score", ((GameFinished) topPointer).getScore());
        return result;
    }

    public Map<String, Integer> getTimeStatisticsInDifficulty(User user, Difficulty difficulty) throws Exception {
        ArrayList<Game> games = gameRepository.getAllGamesByDifficultyAndUser(difficulty, user);
        float bestTime = -1;
        float avgTime = -1;
        int gameCount = 0;
        for (Game game : games) {
            if (game instanceof GameFinished && !((GameFinished) game).isSurrendered()) {
                if (bestTime == -1 || game.getTimeSpent() < bestTime) bestTime = game.getTimeSpent();
                if (avgTime == -1) avgTime = 0;
                gameCount++;
                avgTime += game.getTimeSpent();
            }
        }
        if (avgTime != -1) avgTime /= gameCount;

        Map<String, Integer> result = new HashMap<>();
        result.put("bestTime", (int)bestTime);
        result.put("avgTime", (int)avgTime);
        return result;
    }

    public void saveGame(Game game) throws Exception {
        User user = userRepository.getUser(game.getPlayerName());
        if (user == null) {
            throw new Exception("User not found");
        }

        gameRepository.saveGame(game);
    }

    public void deleteGame(Game game) throws Exception {
        User user = userRepository.getUser(game.getPlayerName());
        if (user == null) {
            throw new Exception("User not found");
        }

        gameRepository.deleteGame(game);
    }
}
