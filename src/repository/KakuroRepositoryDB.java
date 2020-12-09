package src.repository;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;

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
        // Returns null if kakuro is not found
        ArrayList<Kakuro> kakuros = getAllKakuros();
        for (Kakuro k : kakuros) if (k.getId().equals(id)) return k;

        return null;
    }

    @Override
    public void deleteKakuro(Kakuro kakuro) throws IOException {
        ArrayList<Kakuro> kakuroList = this.getAllKakuros();
        for (int i = 0; i<kakuroList.size(); i++) {
            if (kakuroList.get(i).getId().equals(kakuro.getId())) {
                kakuroList.remove(i);
                driver.writeToFile(kakuroList, "kakuro");
                return;
            }
        }
    }

    @Override
    public void saveKakuro(Kakuro kakuro) throws IOException {
        ArrayList<Kakuro> kakuroList = this.getAllKakuros();

        for (int i = 0; i<kakuroList.size(); i++) {
            if (kakuroList.get(i).getId().equals(kakuro.getId())) {
                kakuroList.set(i, kakuro);
                driver.writeToFile(kakuroList, "kakuro");
                return;
            }
        }

        kakuroList.add(kakuro);
        driver.writeToFile(kakuroList, "kakuro");
    }

    @Override
    public ArrayList<Kakuro> getAllKakuros() throws IOException {
        return (ArrayList<Kakuro>)(ArrayList<?>) driver.readAll(Kakuro.class);
    }

    @Override
    public ArrayList<Kakuro> getAllKakurosByUser(User user) throws IOException {
        ArrayList<Kakuro> kakuros = getAllKakuros();
        ArrayList<Kakuro> res = new ArrayList<>();
        for (Kakuro k : kakuros) if (k.getUserName().equals(user.getName())) res.add(k);

        return res;
    }

    @Override
    public ArrayList<Kakuro> getAllKakurosByDifficulty(Difficulty difficulty) throws IOException {
        ArrayList<Kakuro> kakuros = getAllKakuros();
        ArrayList<Kakuro> res = new ArrayList<>();
        for (Kakuro k : kakuros) if (k.getDifficulty().equals(difficulty)) res.add(k);

        return res;
    }
}
