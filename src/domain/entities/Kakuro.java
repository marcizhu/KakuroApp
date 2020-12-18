package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class Kakuro {
    private final String name = ""; // TODO: implement as PRIMARY KEY!!
    private final User createdBy;
    private final Timestamp createdAt;
    private final UUID id;
    private final Difficulty difficulty;
    private final Board board;

    // Creates a Kakuro that's not assigned to any user (created by the program)
    public Kakuro (Difficulty difficulty, Board board) {
        this.createdBy = null;
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());;
        this.board = board;
    }

    // Creates a Kakuro assigned to a User
    public Kakuro(Difficulty difficulty, Board board, User createdBy) {
        this.createdBy = createdBy;
        this.board = board;
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Creates a Kakuro with a given Id and creation date assigned to a User (Used by the Deserializer)
    public Kakuro(UUID id, Timestamp createdAt, Difficulty difficulty, Board board, User createdBy) {
        this.createdBy = createdBy;
        this.board = board;
        this.id = id;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
    }

    public String getName() { return this.name; }

    public UUID getId() {
        return this.id;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public User getCreatedBy() { return this.createdBy; } // FIXME: remove

    public User getUser() { return this.createdBy; }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Board getBoard() {
        return this.board;
    }

    public String toString() {
        String author = createdBy == null ? "null" : createdBy.toString();

        return "Id: " + id + ", created by {" + author + "}, created at: "
                + createdAt  + ", difficulty: " + difficulty + "\nBoard: " + board.getId().toString() + "\n" + board.toString();
    }
}
