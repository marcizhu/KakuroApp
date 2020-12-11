package src.repository;

import src.domain.entities.User;
import src.repository.serializers.UserSerializer;

import java.io.IOException;
import java.util.ArrayList;


public class UserRepositoryDB implements UserRepository {
    private final DB driver;

    public UserRepositoryDB(DB driver) {
        this.driver = driver;
    }

    @Override
    public User getUser(String userName) throws IOException {
        // Returns null if user is not found
        ArrayList<User> users = getAllUsers();
        for (User u : users) if (u.getName().equals(userName)) return u;

        return null;
    }

    @Override
    public void deleteUser (User user) throws IOException {
        ArrayList<User> usersList = this.getAllUsers();
        for (int i = 0; i<usersList.size(); i++) {
            if (usersList.get(i).getName().equals(user.getName())) {
                usersList.remove(i);
                driver.writeToFile(usersList, "User");
                return;
            }
        }
    }

    @Override
    public void saveUser (User user) throws IOException {
        ArrayList<User> usersList = this.getAllUsers();

        for (int i = 0; i<usersList.size(); i++) {
            if (usersList.get(i).getName().equals(user.getName())) {
                usersList.set(i, user);
                driver.writeToFile(usersList, "User");
                return;
            }
        }

        usersList.add(user);
        driver.writeToFile(usersList, "User");
    }

    @Override
    public ArrayList<User> getAllUsers () throws IOException {
        return (ArrayList<User>)(ArrayList<?>) driver.readAll(User.class);
    }
}
