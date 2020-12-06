package src.repository;

import src.domain.User;
import src.repository.DB;
import src.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;


public class UserRepositoryDB implements UserRepository {
    DB driver;

    public UserRepositoryDB(DB driver) {
        this.driver = driver;
    }

    @Override
    public User getUser(String userName) throws IOException {
        // Returns null if user is not found
        return (User)driver.readObject(User.class, userName);
    }

    @Override
    public void deleteUser (User user) throws IOException {
        driver.deleteObject(user);
    }

    @Override
    public void saveUser (User user) throws IOException {
        driver.writeObject(user);
    }

    @Override
    public ArrayList<User> getAllUsers () throws IOException {
        ArrayList<Object> o = driver.readAll(User.class);
        System.out.println("List of all objects:" + o);
        ArrayList<User> res = new ArrayList<>();
        System.out.println(res.size());
        for (int i = 0; i<o.size(); i++) {
            res.add((User)o.get(i));
        }

        return res;
    }
}
