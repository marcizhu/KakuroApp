package src.repository;

import src.domain.Kakuro;

import java.io.IOException;

public interface KakuroRepository {
    Kakuro getKakuro (String id) throws IOException;
    void deleteKakuro (Kakuro kakuro) throws IOException;
    void saveKakuro (Kakuro kakuro) throws IOException;
}
