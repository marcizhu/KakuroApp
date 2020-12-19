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

    public Game getGameInProgress(String username, String kakuroname) throws Exception {
        ArrayList<Game> userGames = gameRepository.getAllGamesByUser(username);
        for (Game game : userGames) {
            if (game.getKakuroName() == kakuroname && game instanceof GameInProgress) {
                return game;
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
                    return ((GameFinished) a).getTimeFinished().compareTo(((GameFinished) b).getTimeFinished());
                } else { // !aFinished && !bFinished
                    return ((GameInProgress) a).getLastPlayed().compareTo(((GameInProgress) b).getLastPlayed());
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
            gameData.put("timeSpent", game.getTimeSpent());
            if (game instanceof GameInProgress) {
                gameData.put("state", "unfinished");
            } else { // game instanceof GameFinished
                gameData.put("score", ((GameFinished)game).getScore());
                gameData.put("state", "finished");
            }
        }
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
