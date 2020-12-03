package src.repository;

import src.domain.User;

public interface UserRepository {
    User getUserByName(String name);
}
