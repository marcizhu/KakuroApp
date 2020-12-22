package src.repository;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;

import java.io.IOException;
import java.util.ArrayList;

public interface KakuroRepository {

    /**
     * Returns a Kakuro with the given name
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param name  Name that identifies the Kakuro that we want to get
     * @return the Kakuro with the given name or null if it does not exist
     */
    Kakuro getKakuro (String name) throws IOException;

    /**
     * Deletes a Kakuro
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param kakuro  the Kakuro that we want to delete
     */
    void deleteKakuro (Kakuro kakuro) throws IOException;

    /**
     * Deletes a Kakuro
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param kakuroName  Name that identifies the kakuro that we want to delete
     */
    void deleteKakuro (String kakuroName) throws IOException;

    /**
     * Saves a Kakuro and its Board
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param kakuro  The kakuro that will be saved
     */
    void saveKakuro (Kakuro kakuro) throws IOException;

    /**
     * Returns all kakuros that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @return ArrayList of Kakuro objects with all the kakuros that have been persisted
     */
    ArrayList<Kakuro> getAllKakuros () throws IOException;

    /**
     * Returns all kakuros created by the given User that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param user The owner of the Kakuros
     * @return ArrayList of Kakuro objects with all the kakuros created by user that have been persisted
     */
    ArrayList<Kakuro> getAllKakurosByUser (User user) throws IOException;

    /**
     * Returns all Kakuros with the given Difficulty that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param difficulty the Difficulty of the kakuros
     * @return ArrayList of Kakuro objects with the given Difficulty that have been persisted
     */
    ArrayList<Kakuro> getAllKakurosByDifficulty (Difficulty difficulty) throws IOException;
}
