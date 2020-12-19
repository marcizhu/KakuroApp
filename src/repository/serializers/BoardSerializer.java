package src.repository.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import src.domain.entities.Board;

import java.lang.reflect.Type;

public class BoardSerializer implements JsonSerializer<Board> {
    @Override
    public JsonElement serialize(Board board, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", board.getId().toString());
        obj.addProperty("width", board.getWidth());
        obj.addProperty("height", board.getHeight());
        obj.addProperty("cells", board.cellsToString());
        obj.addProperty("notations", board.notationsToString());
        return obj;
    }
}
