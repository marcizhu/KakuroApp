package src.repository;

import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.serializers.KakuroDeserializer;
import src.repository.serializers.KakuroSeializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class KakuroRepositoryDB implements KakuroRepository {

    private final DB driver;
    private final KakuroSeializer serializer;
    private final KakuroDeserializer deserializer;

    public KakuroRepositoryDB (DB driver) {
        this.driver = driver;
        this.serializer = new KakuroSeializer();
        this.deserializer = new KakuroDeserializer();
    }

    @Override
    public Kakuro getKakuro(String name) throws IOException {
        // Returns null if kakuro is not found
        ArrayList<Kakuro> kakuros = getAllKakuros();
        for (Kakuro k : kakuros) if (k.getName().equals(name)) return k;

        return null;
    }

    @Override
    public void deleteKakuro(Kakuro kakuro) throws IOException {
        deleteKakuro(kakuro.getName());
    }

    @Override
    public void deleteKakuro(String kakuroName) throws IOException {
        ArrayList<Kakuro> kakuroList = this.getAllKakuros();
        for (int i = 0; i<kakuroList.size(); i++) {
            if (kakuroList.get(i).getName().equals(kakuroName)) {
                kakuroList.remove(i);
                driver.writeToFile(kakuroList, "Kakuro", serializer, Kakuro.class);
                return;
            }
        }
    }

    @Override
    public void saveKakuro(Kakuro kakuro) throws IOException {
        ArrayList<Kakuro> kakuroList = this.getAllKakuros();

        for (int i = 0; i<kakuroList.size(); i++) {
            if (kakuroList.get(i).getName().equals(kakuro.getName())) {
                kakuroList.set(i, kakuro);
                driver.writeToFile(kakuroList, "Kakuro", serializer, Kakuro.class);
                return;
            }
        }

        kakuroList.add(kakuro);
        driver.writeToFile(kakuroList, "Kakuro", serializer, Kakuro.class);

        BoardRepository boardRepo = new BoardRepositoryDB(driver);
        boardRepo.saveBoard(kakuro.getBoard());
    }

    @Override
    public ArrayList<Kakuro> getAllKakuros() throws IOException {
        return (ArrayList<Kakuro>)(ArrayList<?>) driver.readAll(Kakuro.class, deserializer);
    }

    @Override
    public ArrayList<Kakuro> getAllKakurosByUser(User user) throws IOException {
        ArrayList<Kakuro> kakuros = getAllKakuros();
        ArrayList<Kakuro> res = new ArrayList<>();
        for (Kakuro k : kakuros) {
            if (user == null && k.getCreatedBy() == null) {
                res.add(k);
            } else if (k.getCreatedBy() == null) continue;
            else if (k.getCreatedBy().getName().equals(user.getName())) res.add(k);
        }

        return res;
    }

    @Override
    public ArrayList<Kakuro> getAllKakurosUserCreated() throws IOException {
        //TODO:
        return null;
    }

    @Override
    public ArrayList<Kakuro> getAllKakurosByDifficulty(Difficulty difficulty) throws IOException {
        ArrayList<Kakuro> kakuros = getAllKakuros();
        ArrayList<Kakuro> res = new ArrayList<>();
        for (Kakuro k : kakuros) if (k.getDifficulty().equals(difficulty)) res.add(k);

        return res;
    }
}
