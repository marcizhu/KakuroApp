package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class Kakuro {
    private final String createdBy;
    private final Timestamp createdAt;
    private final UUID id;
    private final Difficulty difficulty;
    private final UUID boardId;
    private final Board board; // TODO: remove this?

    public Kakuro (Difficulty difficulty, Board board) {
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());;
        this.boardId = board.getId();
        this.createdBy = "asdf"; // TODO: fix thissssss
        this.board = board;
    }

    public Kakuro (Difficulty difficulty, Board board, User createdBy) {
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());;
        this.boardId = board.getId();
        this.createdBy = createdBy.getName();
        this.board = board;
    }

    public Kakuro(Difficulty difficulty, UUID boardId, String createdBy, Board board) {
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

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Board getBoard() {
        return this.board;
    }

    public String toString() {
        return "Id: " + id + ", created by: " + createdBy + ", created at: "
                + createdAt + ", board Id: " + boardId + ", difficulty: " + difficulty;
    }
}
