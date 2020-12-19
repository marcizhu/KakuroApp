package src.domain.controllers;

import src.domain.entities.Game;
import src.domain.entities.GameInProgress;
import src.domain.entities.User;
import src.repository.GameRepository;
import src.repository.UserRepository;

import java.util.ArrayList;

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
