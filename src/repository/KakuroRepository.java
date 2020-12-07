package src.repository;

import src.domain.Difficulty;
import src.domain.Kakuro;
import src.domain.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public interface KakuroRepository {
    Kakuro getKakuro (UUID id) throws IOException;
    void deleteKakuro (Kakuro kakuro) throws IOException;
    void saveKakuro (Kakuro kakuro) throws IOException;
    ArrayList<Kakuro> getAllKakuros () throws IOException;
    ArrayList<Kakuro> getAllKakurosByUser (User user) throws IOException;
    ArrayList<Kakuro> getAllKakurosByDifficulty (Difficulty difficulty) throws IOException;
}
