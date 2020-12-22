package src.domain.entities;

import java.sql.Timestamp;

public class Kakuro {
    private final String seed;
    private final String name;
    private final User createdBy;
    private final Timestamp createdAt;
    private final Difficulty difficulty;
    private final Board board;
    private final int colorCode;

    // Creates a Kakuro that's not assigned to any user (created by the program)
    public Kakuro (String name, Difficulty difficulty, Board board, String seed) {
        this(name, new Timestamp(System.currentTimeMillis()), difficulty, board, null, seed, 0);
    }

    public Kakuro (String name, Difficulty difficulty, Board board, String seed, int colorCode) {
        this(name, new Timestamp(System.currentTimeMillis()), difficulty, board, null, seed, colorCode);
    }

    // Creates a Kakuro assigned to a User
    public Kakuro (String name, Difficulty difficulty, Board board, User createdBy, String seed) {
        this(name, new Timestamp(System.currentTimeMillis()), difficulty, board, createdBy, seed, 0);
    }

    public Kakuro (String name, Difficulty difficulty, Board board, User createdBy, String seed, int colorCode) {
        this(name, new Timestamp(System.currentTimeMillis()), difficulty, board, createdBy, seed, colorCode);
    }

    // Creates a Kakuro with a given creation date assigned to a User (Used by the Deserializer)
    public Kakuro (String name, Timestamp createdAt, Difficulty difficulty, Board board, User createdBy, String seed) {
        this(name, createdAt, difficulty, board, createdBy, seed, 0);
    }

    // Creates Kakuro with all fields defined. General constructor
    public Kakuro (String name, Timestamp createdAt, Difficulty difficulty, Board board, User createdBy, String seed, int colorCode) {
        this.seed = seed;
        this.name = name;
        this.createdBy = createdBy;
        this.board = board;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
        this.colorCode = colorCode;
    }

    public String getName() {
        return this.name;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Board getBoard() {
        return this.board;
    }

    public String toString() {
        String author = createdBy == null ? "null" : createdBy.toString();

        return "Name: " + name + ", created by {" + author + "}, created at: "
                + createdAt  + ", difficulty: " + difficulty + "\nBoard: " + board.getId().toString() + "\n" + board.toString();
    }

    public String getSeed() {
        return seed == null ? "" : this.seed;
    }

    public int getColorCode() { return colorCode; }
}
