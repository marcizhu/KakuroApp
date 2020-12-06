package src.repository;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;
import java.io.FileWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;


public class DB {
    private final String path;
    public DB() {
        this.path = "data/DB/";
    }

    public DB (String path) {
        this.path = path;
    }

    public ArrayList<Object> readAll(Class objectClass) throws IOException {
        String fileContents;
        Gson gson = new Gson();
        try {
            fileContents = Files.readString(Path.of(path + objectClass.getSimpleName() + ".json"));
        } catch (NoSuchFileException e) {
            System.err.println("Class " + objectClass.getSimpleName() + " has no entry in the database");
            e.printStackTrace();
            return null;
        }

        Type collectionType = TypeToken.getParameterized(ArrayList.class, objectClass).getType(); // FIXME:
        return gson.fromJson(fileContents, collectionType);
    }


    public void writeToFile(Collection<?> col, String fileName) throws IOException {
        Gson g = new Gson();
        String rawJSON = g.toJson(col);
        FileWriter writer = new FileWriter(path + fileName + ".json");

        writer.write(rawJSON);
        writer.close();
    }

}
