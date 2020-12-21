package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.UUID;

public class KakuroDeserializer implements JsonDeserializer<Kakuro> {

    @Override
    public Kakuro deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();

        DB driver = new DB();
        UserRepository userRepo = new UserRepositoryDB(driver);
        BoardRepository boardRepo = new BoardRepositoryDB(driver);

        Timestamp createdAt = Timestamp.valueOf(obj.get("createdAt").getAsString());
        Difficulty d = Difficulty.valueOf(obj.get("difficulty").getAsString());
        String name = obj.get("name").getAsString();
        String seed = obj.get("seed").getAsString();
        UUID boardId = UUID.fromString(obj.get("boardId").getAsString());

        Board b;

        try {
            b = boardRepo.getBoard(boardId);
        } catch (IOException e) {
            System.err.println("Error getting board " + boardId.toString() + " from database");
            e.printStackTrace();
            b = null;
        }

        User u = null;
        if (obj.get("createdBy") != null) {
            String createdBy = obj.get("createdBy").getAsString();

            try {
                u = userRepo.getUser(createdBy);
            } catch (IOException e) {
                System.err.println("Error getting user " + createdBy + " from database");
                e.printStackTrace();
                u = null;
            }
        }

        return new Kakuro(name, createdAt, d, b, u, seed);
    }
}
