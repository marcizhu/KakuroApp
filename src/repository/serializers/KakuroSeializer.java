package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.Kakuro;

import java.lang.reflect.Type;

public class KakuroSeializer implements JsonSerializer<Kakuro> {

    @Override
    public JsonElement serialize(Kakuro kakuro, Type type, JsonSerializationContext jsonSerializationContext) {
        System.out.println("We gone serialize this kakuro");
        JsonObject obj = new JsonObject();
        obj.addProperty("id", kakuro.getId().toString());
        obj.addProperty("createdAt", kakuro.getCreatedAt().toString());
        obj.addProperty("difficulty", kakuro.getDifficulty().toString());
        obj.addProperty("boardId", kakuro.getBoard().getId().toString());
        obj.addProperty("createdBy", kakuro.getUser().getName());

        return obj;
    }
}
