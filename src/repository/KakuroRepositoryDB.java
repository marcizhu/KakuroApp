package src.repository;

import src.domain.Difficulty;
import src.domain.Kakuro;
import src.domain.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class KakuroRepositoryDB implements KakuroRepository {

    private final DB driver;

    public KakuroRepositoryDB (DB driver) {
        this.driver = driver;
    }

    @Override
    public Kakuro getKakuro(UUID id) throws IOException {
        //TODO
        return null;
    }

    @Override
    public void deleteKakuro(Kakuro kakuro) throws IOException {
        // TODO
    }

    @Override
    public void saveKakuro(Kakuro kakuro) throws IOException {
        //TODO
    }

    @Override
    public ArrayList<Kakuro> getAllKakuros() throws IOException {
        return (ArrayList<Kakuro>)(ArrayList<?>) driver.readAll(Kakuro.class);
    }

    @Override
    public ArrayList<Kakuro> getAllKakurosByUser(String userName) throws IOException {
        // TODO
        return null;
    }

    @Override
    public ArrayList<Kakuro> getAllKakurosByDifficulty(Difficulty difficulty) throws IOException {
        // TODO
        return null;
    }
}
