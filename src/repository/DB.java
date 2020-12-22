package src.repository;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;
import java.io.FileWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class DB {
    private final String path;
    public DB() {
        this.path = "data/DB/";
    }

    public DB (String path) {
        this.path = path;
    }

    public ArrayList<Object> readAll(Class objectClass, JsonDeserializer deserializer) throws IOException {
        String fileContents;
        Gson gson;
        if (deserializer != null ) gson = new GsonBuilder().registerTypeAdapter(objectClass, deserializer).create();
        else gson = new Gson();

        try {
            fileContents = Files.readString(Path.of(path + objectClass.getSimpleName() + ".json"));
        } catch (NoSuchFileException e) {
            System.err.println("Class " + objectClass.getSimpleName() + " has no entry in the database");
            e.printStackTrace();
            return null;
        }

        Type collectionType = TypeToken.getParameterized(ArrayList.class, objectClass).getType();
        ArrayList<Object> res = gson.fromJson(fileContents, collectionType);
        if (res == null) return new ArrayList<Object>();
        return res;
    }

    public ArrayList<Object> readAll(Class objectClass) throws IOException {
        return readAll(objectClass, null);
    }

    public void writeToFile(Collection<?> col, String fileName, JsonSerializer serializer, Class serializedClass) throws IOException {
        ArrayList<Class> serializedClasses = new ArrayList<>();
        serializedClasses.add(serializedClass);
        writeToFile(col, fileName, serializer, serializedClasses);
    }

    public void writeToFile(Collection<?> col, String fileName, JsonSerializer serializer, ArrayList<Class> serializedClasses) throws IOException {
        Gson g;

        if (serializer != null) {
            GsonBuilder builder = new GsonBuilder();
            for (Class c : serializedClasses) builder.registerTypeAdapter(c, serializer);
            g = builder.create();
        }
        else g = new Gson();

        String rawJSON = g.toJson(col);

        FileWriter writer = new FileWriter(path + fileName + ".json", false);

        writer.write(rawJSON);
        writer.close();
    }

    public void writeToFile(Collection<?> col, String fileName) throws IOException {
        writeToFile(col, fileName, null, Object.class);
    }
}
