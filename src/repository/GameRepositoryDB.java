package src.repository;

import src.domain.entities.Game;
import src.domain.entities.GameFinished;
import src.domain.entities.GameInProgress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/*
    FIXME:
    when deserializing games, Gson will try to instantiate them with the Game constructor,
    which will throw an exception because Game is abstract and cannot be instantiated.
    Here is the fix TODO: https://www.javacodegeeks.com/2012/04/json-with-gson-and-abstract-classes.html
 */

public class GameRepositoryDB implements GameRepository {

    private final DB driver;

    public GameRepositoryDB (DB driver) {
        this.driver = driver;
    }

    @Override
    public Game getGame(UUID gameId) throws IOException {
        // TODO:
        return null;
    }

    @Override
    public void deleteGame(UUID gameId) throws IOException {
        // TODO:
    }

    @Override
    public void saveGame(Game game) throws IOException {
        // FIXME:
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
