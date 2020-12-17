package src.repository;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public interface KakuroRepository {
    Kakuro getKakuro (UUID id) throws IOException;
    void deleteKakuro (Kakuro kakuro) throws IOException;
    void deleteKakuro (UUID kakuroID) throws IOException;
    void saveKakuro (Kakuro kakuro) throws IOException;
    ArrayList<Kakuro> getAllKakuros () throws IOException;
    ArrayList<Kakuro> getAllKakurosByUser (User user) throws IOException; // Get all kakuros created by a concrete user
    ArrayList<Kakuro> getAllKakurosUserCreated () throws IOException; // Get all kakuros created by any user TODO
    ArrayList<Kakuro> getAllKakurosByDifficulty (Difficulty difficulty) throws IOException;
}
