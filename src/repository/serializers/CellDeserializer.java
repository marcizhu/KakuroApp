package src.repository.serializers;

import com.google.gson.*;
import src.domain.entities.*;

import java.lang.reflect.Type;

public class CellDeserializer implements JsonDeserializer<Cell[][]> {
    private final int width, height;

    public CellDeserializer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Cell[][] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray arr = jsonElement.getAsJsonArray();
        Cell[][] cells = new Cell[height][width];

        for (int i = 0; i < height; i++) {
            JsonArray r = arr.get(i).getAsJsonArray();

            for (int j = 0; j < width; ++j) {
                if (r.get(j).isJsonNull()) {
                    cells[i][j] = null;
                    continue;
                }
                JsonObject obj = r.get(j).getAsJsonObject();

                boolean isBlack = obj.getAsJsonObject().get("value") == null;

                int row = obj.getAsJsonObject().get("coordRow").getAsInt();
                int col = obj.getAsJsonObject().get("coordCol").getAsInt();

                if (isBlack) cells[i][j] = deserializeBlackCell(row, col, obj.getAsJsonObject());
                else cells[i][j] = deserializeWhiteCell(row, col, obj);
            }
        }
        return cells;
    }

    WhiteCell deserializeWhiteCell(int row, int col, JsonObject obj) {
        int value = obj.get("value").getAsInt();
        int notations = obj.get("notations").getAsInt();

        return new WhiteCell(row, col, value, notations);
    }

    BlackCell deserializeBlackCell(int row, int col, JsonObject obj) {
        int vSum = obj.get("verticalSum").getAsInt();
        int hSum = obj.get("horizontalSum").getAsInt();

        return new BlackCell(row, col, vSum, hSum);
    }
}
