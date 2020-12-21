package src.repository.serializers;

import com.google.gson.*;
import src.domain.controllers.Reader;
import src.domain.entities.*;

import java.lang.reflect.Type;
import java.util.UUID;

public class BoardDeserializer implements JsonDeserializer<Board> {

    @Override
    public Board deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();

        UUID id = UUID.fromString(obj.get("id").getAsString());

        int width = obj.get("width").getAsInt();
        int height = obj.get("height").getAsInt();

        String cells = obj.get("cells").getAsString();
        String formattedBoard = height + "," + width + "\n" + cells;
        String notations = obj.get("notations").getAsString();

        return Reader.fromString(id, notations, formattedBoard);
    }
}