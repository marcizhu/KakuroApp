package src.domain;

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

    public int getPlayedGames() {
        // TODO: data repository is needed to implement
        return 0;
    }

    public int getKakurosCreaed() {
        // TODO: data repository is needed to implement
        return 0;
    }

    public String toString() {
        return "name: " + name + ", score: " + score;
    }
}
