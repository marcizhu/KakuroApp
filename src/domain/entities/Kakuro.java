package src.domain.entities;

import java.util.UUID;

public class Kakuro {
    private final UUID id;
    private final Difficulty difficulty;
    private final Board board;

    public Kakuro(Difficulty difficulty, Board board) {
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.board = board;
    }

    public Kakuro (Difficulty difficulty, int width, int height) {
        // TODO
        this.id = UUID.randomUUID();
        this.difficulty = difficulty;
        this.board = new Board(width, height);
    }

    public UUID getId() {
        return this.id;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public Board getBoard() {
        return this.board;
    }
}
