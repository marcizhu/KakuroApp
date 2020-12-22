package src.repository;

import src.domain.entities.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public interface GameRepository {
    /**
     * Returns a Game with the given identifier
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param gameId  Name that identifies the Kakuro that we want to get
     * @return the Game with the given id or null if it does not exist
     */
    Game getGame (UUID gameId) throws IOException;

    /**
     * Deletes a Game with the given identifier
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param gameId  the identifier of the Game that we want to delete
     */
    void deleteGame (UUID gameId) throws IOException;

    /**
     * Deletes a Game
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param game  the Game that we want to delete
     */
    void deleteGame (Game game) throws IOException;

    /**
     * Saves a Game (and its Board if it is a GameInProgress)
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param game  The Game that will be saved
     */
    void saveGame (Game game) throws IOException;

    /**
     * Returns all games played by the given user that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param user the User that has played the games
     * @return ArrayList of Game objects with all the games by that user that have been persisted
     */
    ArrayList<Game> getAllGamesByUser (User user) throws IOException;

    /**
     * Returns all games played by the given user that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param userName the name of the User that has played the games
     * @return ArrayList of Game objects with all the games by that user that have been persisted
     */
    ArrayList<Game> getAllGamesByUser (String userName) throws IOException;

    /**
     * Returns all games played in the given kakuro that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param kak the Kakuro in which the games were played
     * @return ArrayList of Game objects with all the games by that user that have been persisted
     */
    ArrayList<Game> getAllGamesInKakuro (Kakuro kak) throws IOException;

    /**
     * Returns all games played in the given kakuro that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param kakuroName the name of the Kakuro in which the games were played
     * @return ArrayList of Game objects with all the games by that user that have been persisted
     */
    ArrayList<Game> getAllGamesInKakuro (String kakuroName) throws IOException;

    /**
     * Returns all Games that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @return ArrayList of Game objects with all the games that have been persisted
     */
    ArrayList<Game> getAllGames () throws IOException;

    /**
     * Returns all games played with the given Difficulty that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param diff the Difficulty of the Games
     * @return ArrayList of Game objects with the given Difficulty that have been persisted
     */
    ArrayList<Game> getAllGamesByDifficulty (Difficulty diff) throws IOException;

    /**
     * Returns all games played by the given User and with the given Difficultythat have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param diff the Difficulty of the Games
     * @param user the User that plays the Games
     * @return ArrayList of Game objects played by the given User and with the given Difficulty that have been persisted
     */
    ArrayList<Game> getAllGamesByDifficultyAndUser (Difficulty diff, User user) throws IOException;

    /**
     * Returns all Games in progress that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @return ArrayList of GameInProgress objects with all the Games in progress that have been persisted
     */
    ArrayList<GameInProgress> getAllGamesInProgress () throws IOException;

    /**
     * Returns all finished Games that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @return ArrayList of GameFinished objects with all the finished Games that have been persisted
     */
    ArrayList<GameFinished> getAllGamesFinished () throws IOException;
}
