package src.repository;

import src.domain.entities.*;
import src.repository.serializers.GameDeserializer;
import src.repository.serializers.GameSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


public class GameRepositoryDB implements GameRepository {

    private final DB driver;
    private final GameSerializer serializer;
    private final GameDeserializer deserializer;
    private final ArrayList<Class> subclasses;
    private final BoardRepository boardRepository;

    public GameRepositoryDB (DB driver) {
        this.driver = driver;
        this.serializer = new GameSerializer();
        this.deserializer = new GameDeserializer();
        this.subclasses = new ArrayList<>();
        this.boardRepository = new BoardRepositoryDB(driver);
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
                Game g = gamesList.get(i);
                gamesList.remove(i);
                if (g instanceof GameInProgress) {
                    boardRepository.deleteBoard(((GameInProgress) g).getBoard());
                }
                driver.writeToFile(gamesList, "Game", serializer, subclasses);
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
                if (game instanceof GameInProgress) {
                    // Save board
                    BoardRepository boardRepo = new BoardRepositoryDB(driver);
                    boardRepo.saveBoard(((GameInProgress)game).getBoard());
                }
                driver.writeToFile(gamesList, "Game", serializer, subclasses);
                return;
            }
        }

        gamesList.add(game);
        driver.writeToFile(gamesList, "Game", serializer, subclasses);

        if (game instanceof GameInProgress) {
            // Save board
            BoardRepository boardRepo = new BoardRepositoryDB(driver);
            boardRepo.saveBoard(((GameInProgress)game).getBoard());
        }
    }

    @Override
    public ArrayList<Game> getAllGamesByUser(User user) throws IOException {
        return getAllGamesByUser(user.getName());
    }

    @Override
    public ArrayList<Game> getAllGamesByUser(String userName) throws IOException {
        ArrayList<Game> allGames = getAllGames();
        ArrayList<Game> res = new ArrayList<>();

        for (Game g : allGames) {
            if (g.getPlayerName().equals(userName)) {
                res.add(g);
            }
        }

        return res;
    }

    @Override
    public ArrayList<Game> getAllGamesInKakuro(Kakuro kak) throws IOException {
        return getAllGamesInKakuro(kak.getName());
    }

    @Override
    public ArrayList<Game> getAllGamesInKakuro(String kakuroName) throws IOException {
        ArrayList<Game> allGames = getAllGames();
        ArrayList<Game> res = new ArrayList<>();

        for (Game g : allGames) {
            if (g.getKakuro().getName().equals(kakuroName)) res.add(g);
        }

        return res;
    }

    @Override
    public float getBestTime(String kakuroName) throws IOException {
        // TODO maybe implement this in the use case
        return 0;
    }

    @Override
    public int getKakuroState(String kakuroName) throws IOException {
        // TODO:
        return 0;
    }

    @Override
    public ArrayList<Game> getAllGames() throws IOException {
        return (ArrayList<Game>)(ArrayList<?>) driver.readAll(Game.class, deserializer);
    }

    @Override
    public ArrayList<Game> getAllGamesByDifficultyAndUser(Difficulty diff, User user) throws IOException {
        ArrayList<Game> games = getAllGamesByDifficulty(diff);
        ArrayList<Game> res = new ArrayList<>();

        for (Game g : games) if (g.getPlayerName().equals(user.getName())) res.add(g);

        return res;
    }

    @Override
    public ArrayList<Game> getAllGamesByDifficulty(Difficulty diff) throws IOException {
        ArrayList<Game> allGames = getAllGames();
        ArrayList<Game> res = new ArrayList<>();

        for (Game g : allGames) if (g.getKakuro().getDifficulty() == diff) res.add(g);

        return res;
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
