package src.repository;

import src.domain.User;
import src.repository.DB;
import src.repository.UserRepository;


public class UserRepositoryDB implements UserRepository{
    DB driver;

    public void UserRepository(DB driver) {
        this.driver = driver;
    }

    @Override
    public User getUserByName(String name) {
        return null; //TODO
    }
}
