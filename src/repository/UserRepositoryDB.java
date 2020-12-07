package src.repository;

import src.domain.Kakuro;
import src.domain.User;
import src.repository.DB;
import src.repository.UserRepository;

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
                driver.writeToFile(usersList, "user");
                return;
            }
        }
    }

    @Override
    public void saveUser (User user) throws IOException {
        ArrayList<User> usersList = this.getAllUsers();
        System.out.println(usersList);
        for (int i = 0; i<usersList.size(); i++) {
            if (usersList.get(i).getName().equals(user.getName())) {
                System.out.println("Found match!!");
                usersList.set(i, user);
                driver.writeToFile(usersList, "user");
                return;
            }
        }

        usersList.add(user);
        driver.writeToFile(usersList, "user");
    }

    @Override
    public ArrayList<User> getAllUsers () throws IOException {
        /*
        ArrayList<Object> o = driver.readAll(User.class);
        ArrayList<User> res = new ArrayList<>();
        System.out.println(res.size());
        for (int i = 0; i<o.size(); i++) {
            res.add((User)o.get(i));
        }

         */

        //return res;
        return (ArrayList<User>)(ArrayList<?>) driver.readAll(User.class);
    }
}
