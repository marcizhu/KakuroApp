package src.repository;

import com.google.gson.reflect.TypeToken;

import src.domain.*;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;
import java.io.FileWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.gson.Gson;


// TODO: https://github.com/stleary/JSON-java

// Database invariable: all Objects are stored as a json array
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

    public void writeObject(Object obj) throws IOException{
        if (obj.getClass() == User.class) {
            User user = (User)obj;
            ArrayList<User> users = getAllUsers();

            for (int i = 0; i<users.size(); i++) if (users.get(i).getName().equals(user.getName())) {
                users.set(i, user);
                writeToFile(users, "user");
                return;
            }

            // Object was not in the db
            users.add(user);
            writeToFile(users, "user");
            return;
        }

        System.err.println("Class " + obj.getClass().getName() + " has no entry in the database");
    }

    public void deleteObject(Object obj) throws IOException{
        if (obj.getClass() == User.class) {
            User user = (User)obj;
            ArrayList<User> users = getAllUsers();

            for (int i = 0; i<users.size(); i++) if (users.get(i).getName().equals(user.getName())) {
                users.remove(i);
                writeToFile(users, "user");
                return;
            }

            // Object was not in the db
            System.out.println("User " + user.getName() + " has no entry in the database");
            return;
        }

        System.err.println("Class " + obj.getClass().getName() + " has no entry in the database");
    }

    private void writeToFile(Collection<?> col, String fileName) throws IOException {
        Gson g = new Gson();
        String rawJSON = g.toJson(col);
        FileWriter writer = new FileWriter(path + fileName + ".json");

        writer.write(rawJSON);
        writer.close();
    }

}
