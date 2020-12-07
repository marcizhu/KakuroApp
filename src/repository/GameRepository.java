package src.repository;

import src.domain.Game;
import src.domain.GameFinished;
import src.domain.GameInProgress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public interface GameRepository {
    Game getGame (UUID gameId) throws IOException;
    void deleteGame (UUID gameId) throws IOException;
    void saveGame (Game game) throws IOException;
    ArrayList<Game> getAllGames () throws IOException;
    ArrayList<GameInProgress> getAllGamesInProgress () throws IOException;
    ArrayList<GameFinished> getAllGamesFinished () throws IOException;
}
