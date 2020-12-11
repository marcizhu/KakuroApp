package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class Kakuro {
    private final String name = ""; // TODO: implement as PRIMARY KEY!!
    private final User createdBy;
    private final String userName; // FIXME: remove
    private final Timestamp createdAt;
    private final UUID id;
    private final Difficulty difficulty;
    private final UUID boardId; // FIXME: remove
    private final Board board;

    // Creates a Kakuro that's not assigned to any user (created by the program)
    public Kakuro (Difficulty difficulty, Board board) {
        this.createdBy = null;
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());;
        this.boardId = board.getId(); // FIXME: remove
        this.userName = ""; //createdBy.getName(); // FIXME: remove
        this.board = board;
    }

    // Creates a Kakuro assigned to a User
    public Kakuro(Difficulty difficulty, Board board, User createdBy) {
        this.createdBy = createdBy;
        this.board = board;
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.boardId = board.getId(); // FIXME: remove
        this.userName = createdBy.getName(); // FIXME: remove
    }

    // Creates a Kakuro with a given Id and creation date assigned to a User (Used by the Deserializer)
    public Kakuro(UUID id, Timestamp createdAt, Difficulty difficulty, Board board, User createdBy) {
        this.createdBy = createdBy;
        this.board = board;
        this.id = id;
        this.difficulty = difficulty;
        this.createdAt = createdAt;
        this.boardId = UUID.randomUUID(); // board.getId(); // FIXME: remove
        this.userName = ""; //createdBy.getName(); // FIXME: remove
    }

    // TODO: remove (here for compatibility)
    public Kakuro(Difficulty difficulty, UUID boardId, String userName, Board board) {
        this.createdBy = null;
        this.board = new Board(); // TODO: fix this
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());;
        this.boardId = boardId;
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return this.id;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public UUID getBoardId() {
        return this.boardId;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public User getUser() { return this.createdBy; }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Board getBoard() {
        return this.board;
    }

    public String toString() {
        String author = createdBy == null ? "" : createdBy.toString();

        return "Id: " + id + ", created by: " + author + ", created at: "
                + createdAt  + ", difficulty: " + difficulty + "\nBoard:\n" + board.toString();
    }
}
