package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.Board;
import src.domain.entities.Difficulty;
import src.domain.entities.Kakuro;
import src.domain.entities.User;
import src.repository.DB;
import src.repository.UserRepository;
import src.repository.UserRepositoryDB;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.UUID;

public class KakuroDeserializer implements JsonDeserializer<Kakuro> {

    @Override
    public Kakuro deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        UserRepository repo = new UserRepositoryDB(new DB());

        Timestamp createdAt = Timestamp.valueOf(obj.get("createdAt").getAsString());
        String createdBy = obj.get("createdBy").getAsString();
        Difficulty d = Difficulty.valueOf(obj.get("difficulty").getAsString());
        UUID kakuroId = UUID.fromString(obj.get("id").getAsString());
        Board b = new Board(10, 10); // FIXME: get board with boardId with boardRepository
        User u;
        try {
            u = repo.getUser(createdBy);
        } catch (IOException e) {
            System.err.println("Error getting user " + createdBy + " from database");
            u = null;
        }

        return new Kakuro(kakuroId, createdAt, d, b, u);
    }
}
