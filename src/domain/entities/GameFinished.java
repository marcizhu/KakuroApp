package src.domain.entities;

import java.sql.Timestamp;

public class GameFinished extends Game{
    private float score;
    private final Timestamp timeFinished;

    public GameFinished(User player, Kakuro kakuro, Board board) {
        super(player, kakuro);
        this.timeFinished = new Timestamp(System.currentTimeMillis());
        this.score = 0;
    }

    public float getScore() {
        return this.score;
    }

    public Timestamp getTimeFinished() {
        return this.timeFinished;
    }
}