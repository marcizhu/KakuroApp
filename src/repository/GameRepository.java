package src.repository;

import src.domain.entities.Game;
import src.domain.entities.GameFinished;
import src.domain.entities.GameInProgress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public interface GameRepository {
    Game getGame (UUID gameId) throws IOException;
    void deleteGame (UUID gameId) throws IOException;
    void deleteGame (Game game) throws IOException;
    void saveGame (Game game) throws IOException;
    ArrayList<Game> getAllGames () throws IOException;
    ArrayList<GameInProgress> getAllGamesInProgress () throws IOException;
    ArrayList<GameFinished> getAllGamesFinished () throws IOException;
}
