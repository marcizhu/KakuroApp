package src.repository;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;

import java.io.IOException;
import java.util.ArrayList;

public interface KakuroRepository {
    Kakuro getKakuro (String name) throws IOException;
    void deleteKakuro (Kakuro kakuro) throws IOException;
    void deleteKakuro (String kakuroName) throws IOException;
    void saveKakuro (Kakuro kakuro) throws IOException;
    ArrayList<Kakuro> getAllKakuros () throws IOException;
    ArrayList<Kakuro> getAllKakurosByUser (User user) throws IOException;
    ArrayList<Kakuro> getAllKakurosUserCreated () throws IOException;
    ArrayList<Kakuro> getAllKakurosByDifficulty (Difficulty difficulty) throws IOException;
}
