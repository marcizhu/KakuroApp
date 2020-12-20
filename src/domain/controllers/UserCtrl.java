package src.domain.controllers;

import src.domain.entities.User;
import src.repository.UserRepository;

import java.util.ArrayList;

public class UserCtrl {
    UserRepository userRepository;

    public UserCtrl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String username) throws Exception {
        User user = userRepository.getUser(username);
        if (user == null) {
            throw new Exception("User not found");
        }

        return user;
    }

    public ArrayList<String> getUserList() throws Exception {
        ArrayList<User> userList = userRepository.getAllUsers();
        ArrayList<String> usernameList = new ArrayList<>();
        for (User user : userList) {
            usernameList.add(user.getName());
        }
        return usernameList;
    }

    public boolean loginUser(String username) throws Exception {
        User user = userRepository.getUser(username);
        return user != null;
    }

    public boolean registerUser(String username) throws Exception {
        if (username.equals("System")) throw new Exception("What are you trying to do? You don't have access to System rights!");
        User user = userRepository.getUser(username);
        if (user != null) return false; // user already in the database
        userRepository.saveUser(new User(username));
        return true;
    }
}
