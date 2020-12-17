package src.repository.serializers;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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

        Gson gson = new GsonBuilder().registerTypeAdapter(Cell[][].class, new CellDeserializer(width, height)).create();
        Cell[][] cells = gson.fromJson(obj.get("cells").getAsJsonArray(), new TypeToken<Cell[][]>(){}.getType());

        return new Board(id, width, height, cells);
    }
}