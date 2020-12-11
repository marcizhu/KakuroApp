package src.repository;

import src.domain.entities.User;

import java.io.IOException;
import java.util.ArrayList;

import java.io.IOException;
import java.util.ArrayList;

public interface UserRepository {
    User getUser (String userName) throws IOException;
    void deleteUser (User user) throws IOException;
    void saveUser (User user) throws IOException;
    ArrayList<User> getAllUsers () throws IOException;
}
