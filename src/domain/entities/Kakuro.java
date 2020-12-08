package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class Kakuro {
    private final String userName;
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
        this.userName = "asdf"; // TODO: fix thissssss
        this.board = board;
    }

    public Kakuro (Difficulty difficulty, Board board, User createdBy) {
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());;
        this.boardId = board.getId();
        this.userName = createdBy.getName();
        this.board = board;
    }

    public Kakuro(Difficulty difficulty, UUID boardId, String userName, Board board) {
        this.board = new Board(); // TODO: fix this
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.createdAt = new Timestamp(System.currentTimeMillis());;
        this.boardId = boardId;
        this.userName = userName;
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

    public String getUserName() {
        return this.userName;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public Board getBoard() {
        return this.board;
    }

    public String toString() {
        return "Id: " + id + ", created by: " + userName + ", created at: "
                + createdAt + ", board Id: " + boardId + ", difficulty: " + difficulty;
    }
}
