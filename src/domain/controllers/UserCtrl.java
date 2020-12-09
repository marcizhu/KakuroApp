package src.domain.controllers;

import src.domain.entities.User;
import src.repository.UserRepository;

import java.io.IOException;
import java.util.ArrayList;

public class UserCtrl {
    UserRepository userRepository;

    public UserCtrl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ArrayList<String> getUserList() throws IOException {
        ArrayList<User> userList = userRepository.getAllUsers();
        ArrayList<String> usernameList = new ArrayList<>();
        for (User user : userList) {
            usernameList.add(user.getName());
        }
        return usernameList;
    }

    public boolean loginUser(String username) throws IOException {
        User user = userRepository.getUser(username);
        return user != null;
    }

    public boolean registerUser(String username) throws IOException {
        User user = userRepository.getUser(username);
        if (user != null) return false; // user already in the database
        userRepository.saveUser(new User(username));
        return true;
    }
}
