package src.repository;

import src.domain.entities.*;
import src.repository.serializers.GameDeserializer;
import src.repository.serializers.GameSerializer;
import src.repository.serializers.KakuroSeializer;

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
    private final GameSerializer serializer;
    private final GameDeserializer deserializer;
    private final ArrayList<Class> subclasses;

    public GameRepositoryDB (DB driver) {
        this.driver = driver;
        this.serializer = new GameSerializer();
        this.deserializer = new GameDeserializer();
        this.subclasses = new ArrayList<>();
        subclasses.add(GameInProgress.class);
        subclasses.add(GameFinished.class);
    }

    @Override
    public Game getGame(UUID gameId) throws IOException {
        for (Game g: getAllGames()) if (g.getId().equals(gameId)) return g;
        return null;
    }

    @Override
    public void deleteGame(Game game) throws IOException {
        deleteGame(game.getId());
    }

    @Override
    public void deleteGame(UUID gameId) throws IOException {
        ArrayList<Game> gamesList = this.getAllGames();

        for (int i = 0; i<gamesList.size(); i++) {
            if (gamesList.get(i).getId().equals(gameId)) {
                gamesList.remove(i);
                driver.writeToFile(gamesList, "game", serializer, subclasses);
                return;
            }
        }
    }

    @Override
    public void saveGame(Game game) throws IOException {
        ArrayList<Game> gamesList = this.getAllGames();

        for (int i = 0; i<gamesList.size(); i++) {
            if (gamesList.get(i).getId().equals(game.getId())) {
                gamesList.set(i, game);
                driver.writeToFile(gamesList, "game", serializer, subclasses);
                return;
            }
        }

        gamesList.add(game);
        driver.writeToFile(gamesList, "game", serializer, subclasses);

        if (game instanceof GameInProgress) {
            // Save board
            BoardRepository boardRepo = new BoardRepositoryDB(driver);
            boardRepo.saveBoard(((GameInProgress)game).getBoard());
        }
    }

    @Override
    public ArrayList<Game> getAllGamesByUser(User user) {
        //TODO
        return null;
    }

    @Override
    public ArrayList<Game> getAllGamesByUser(String userName) {
        //TODO
        return null;
    }

    @Override
    public ArrayList<Game> getAllGamesInKakuro(Kakuro kak) {
        //TODO
        return null;
    }

    @Override
    public ArrayList<Game> getAllGamesInKakuro(String kakuroName) {
        //TODO
        return null;
    }

    @Override
    public ArrayList<Game> getAllGames() throws IOException {
        return (ArrayList<Game>)(ArrayList<?>) driver.readAll(Game.class, deserializer);
    }

    @Override
    public ArrayList<GameInProgress> getAllGamesInProgress() throws IOException {
        ArrayList<Game> games = getAllGames();
        ArrayList<GameInProgress> gamesInProgress = new ArrayList<>();
        for (Game g : games) if (g instanceof GameInProgress) gamesInProgress.add((GameInProgress) g);

        return gamesInProgress;
    }

    @Override
    public ArrayList<GameFinished> getAllGamesFinished() throws IOException {
        ArrayList<Game> games = getAllGames();
        ArrayList<GameFinished> finishedGames = new ArrayList<>();
        for (Game g : games) if (g instanceof GameFinished) finishedGames.add((GameFinished) g);

        return finishedGames;
    }
}
