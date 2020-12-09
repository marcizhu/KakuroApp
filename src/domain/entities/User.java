package src.domain.entities;

public class User {
    private final String name;
    private int score;

    public User(String name) {
        this.name = name;
        this.score = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String toString() {
        return "name: " + name + ", score: " + score;
    }

}
