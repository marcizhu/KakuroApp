package src.repository.serializers;


import com.google.gson.*;
import src.domain.entities.*;
import src.repository.DB;
import src.repository.UserRepository;
import src.repository.UserRepositoryDB;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.UUID;

public class BoardDeserializer implements JsonDeserializer<Board> {

    @Override
    public Board deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        System.out.println("deserializing board!");

        JsonObject obj = jsonElement.getAsJsonObject();

        UUID id = UUID.fromString(obj.get("id").getAsString());

        int width = obj.get("width").getAsInt();
        int height = obj.get("height").getAsInt();

        // FIXME: how do I deserialize cells?
        //Cell [][] cells = new Gson().fromJson(obj.get("cells").getAs, Cell[][].class);
        Cell[][] cells = new Cell[width][height]; // BROKEN!!

        return new Board(id, width, height, cells);
    }
}