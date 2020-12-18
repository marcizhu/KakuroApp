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
    ArrayList<Game> getAllGamesByUser (String userName) throws IOException; // TODO:
    ArrayList<Game> getAllGamesInKakuro (Kakuro kak) throws IOException; // TODO:
    ArrayList<Game> getAllGamesInKakuro (String kakuroName) throws IOException; // TODO
    float getBestTime(String kakuroName) throws IOException; //TODO: return the best time in that kakuro by any user
    int getKakuroState(String kakuroName) throws IOException;
    /* TODO:
        - If there is only a Game, and it is in Progress, state is 2 (in progress)
        - If the first game was finished in a legit way, state is 1 (Game passed)
        - If the first game was not finished properly (Asked the solver to solve it) state is 3 (game Failed)
     */
    ArrayList<Game> getAllGames () throws IOException;
    ArrayList<Game> getAllGamesByDifficultyAndUser (Difficulty diff, User user) throws IOException; // TODO:
    ArrayList<Game> getAllGamesByDifficulty (Difficulty diff) throws IOException; // TODO:
    ArrayList<GameInProgress> getAllGamesInProgress () throws IOException;
    ArrayList<GameFinished> getAllGamesFinished () throws IOException;
}
