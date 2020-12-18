package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.Kakuro;

import java.lang.reflect.Type;

public class KakuroSeializer implements JsonSerializer<Kakuro> {

    @Override
    public JsonElement serialize(Kakuro kakuro, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", kakuro.getName());
        obj.addProperty("createdAt", kakuro.getCreatedAt().toString());
        obj.addProperty("difficulty", kakuro.getDifficulty().toString());
        obj.addProperty("boardId", kakuro.getBoard().getId().toString());
        if (kakuro.getUser() == null) obj.addProperty("createdBy", (String)null);
        else obj.addProperty("createdBy", kakuro.getCreatedBy().getName());

        return obj;
    }
}
