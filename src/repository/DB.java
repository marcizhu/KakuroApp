package src.repository;

import com.google.gson.reflect.TypeToken;

import src.domain.User;
import src.domain.Board;
import src.domain.GameFinished;
import src.domain.GameInProgress;
import src.domain.Kakuro;

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

    public Object readObject(Class objectClass, Object primaryKey) throws IOException{

        if (objectClass == User.class) {
            ArrayList<User> users = getAllUsers();
            for (User u: users) if (u.getName().equals(primaryKey)) return u;
            return null; // No user with that PK found
        }

        System.err.println("Class " + objectClass.getName() + " has no entry in the database");
        return null;
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

        Type collectionType = new TypeToken<ArrayList<User>>(){}.getType();
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

    private ArrayList<User> getAllUsers() throws IOException{
        Gson gson = new Gson();
        String fileContents = Files.readString(Path.of(path + "user.json"));

        Type collectionType = new TypeToken<Collection<User>>(){}.getType();
        ArrayList<User> users = gson.fromJson(fileContents, collectionType);
        if (users != null) return users;
        return new ArrayList<User>();
    }

    private void writeToFile(Collection<?> col, String fileName) throws IOException {
        Gson g = new Gson();
        String rawJSON = g.toJson(col);
        FileWriter writer = new FileWriter(path + fileName + ".json");

        writer.write(rawJSON);
        writer.close();
    }

}
