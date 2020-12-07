package src.domain;

import java.util.UUID;

public class GameInProgress extends Game{
    private UUID boardId;

    public GameInProgress(User player, Kakuro kakuro, Board board) {
        super(player, kakuro);
        this.boardId = board.getId();
    }

    public UUID getBoardId() {
        return this.boardId;
    }
}
