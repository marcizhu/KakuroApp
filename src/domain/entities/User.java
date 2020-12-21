package src.domain.entities;

public class User {
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return "name: " + name;
    }

}
