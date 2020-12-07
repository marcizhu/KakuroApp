package src.repository;

import src.domain.Game;
import src.domain.GameFinished;
import src.domain.GameInProgress;
import src.domain.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class GameRepositoryDB implements GameRepository {

    private final DB driver;

    public GameRepositoryDB (DB driver) {
        this.driver = driver;
    }

    @Override
    public Game getGame(UUID gameId) throws IOException {
        return null;
    }

    @Override
    public void deleteGame(UUID gameId) throws IOException {

    }

    @Override
    public void saveGame(Game game) throws IOException {
        ArrayList<Game> gamesList = this.getAllGames();

        for (int i = 0; i<gamesList.size(); i++) {
            if (gamesList.get(i).getId().equals(game.getId())) {
                gamesList.set(i, game);
                driver.writeToFile(gamesList, "game");
                return;
            }
        }

        gamesList.add(game);
        driver.writeToFile(gamesList, "game");
    }

    @Override
    public ArrayList<Game> getAllGames() throws IOException {
        return (ArrayList<Game>)(ArrayList<?>) driver.readAll(Game.class);
    }

    @Override
    public ArrayList<GameInProgress> getAllGamesInProgress() throws IOException {
        return (ArrayList<GameInProgress>)(ArrayList<?>) driver.readAll(Game.class); // FIXME: do this work ?????
    }

    @Override
    public ArrayList<GameFinished> getAllGamesFinished() throws IOException {
        //ArrayList<GameFinished> finishedGames = new ArrayList<>();
        //for (Object o: driver.readAll(Game.class)) if (o instanceof GameFinished) finishedGames.add((GameFinished) o);
        return (ArrayList<GameFinished>)(ArrayList<?>) driver.readAll(Game.class); // FIXME: do this work ?????
    }
}
