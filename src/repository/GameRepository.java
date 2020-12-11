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
    ArrayList<Game> getAllGamesByUser (User user); // TODO:
    ArrayList<Game> getAllGamesByUser (String userName); // TODO:
    ArrayList<Game> getAllGamesInKakuro (Kakuro kak); // TODO:
    ArrayList<Game> getAllGamesInKakuro (String kakuroName); // TODO
    ArrayList<Game> getAllGames () throws IOException;
    ArrayList<GameInProgress> getAllGamesInProgress () throws IOException;
    ArrayList<GameFinished> getAllGamesFinished () throws IOException;
}
