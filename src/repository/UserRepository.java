package src.repository;

import src.domain.entities.User;

import java.io.IOException;
import java.util.ArrayList;

public interface UserRepository {
    /**
     * Returns a User with a given name
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param userName  Name that tidentifies the user that we want to get
     * @return the User with the given name or null if it does not exist
     */
    User getUser (String userName) throws IOException;

    /**
     * Deletes a user
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param user  The user that will be deleted
     */
    void deleteUser (User user) throws IOException;

    /**
     * Saves a user
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @param user  The user that will be saved
     */
    void saveUser (User user) throws IOException;

    /**
     * Returns all users that have been saved
     * @throws IOException if an unexpected problem occurs when reading from the database
     * @return ArrayList of User objects with all the users that have been persisted
     */
    ArrayList<User> getAllUsers () throws IOException;
}
