package src.repository;

import src.domain.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public interface GameRepository {
    Game getGame (UUID gameId) throws IOException;
    void deleteGame (UUID gameId) throws IOException;
    void deleteGame (Game game) throws IOException;
    void saveGame (Game game) throws IOException;
    ArrayList<Game> getAllGamesByUser (User user) throws IOException;
    ArrayList<Game> getAllGamesByUser (String userName) throws IOException;
    ArrayList<Game> getAllGamesInKakuro (Kakuro kak) throws IOException;
    ArrayList<Game> getAllGamesInKakuro (String kakuroName) throws IOException;
    ArrayList<Game> getAllGames () throws IOException;
    ArrayList<Game> getAllGamesByDifficultyAndUser (Difficulty diff, User user) throws IOException;
    ArrayList<Game> getAllGamesByDifficulty (Difficulty diff) throws IOException;
    ArrayList<GameInProgress> getAllGamesInProgress () throws IOException;
    ArrayList<GameFinished> getAllGamesFinished () throws IOException;
}
