package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class GameFinished extends Game{
    private float score;
    private final Timestamp timeFinished;

    public GameFinished(User player, Kakuro kakuro) {
        super(player, kakuro);
        this.timeFinished = new Timestamp(System.currentTimeMillis());
        this.score = 0;
    }

    // For deserializing
    public GameFinished(UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro, float score, Timestamp timeFinished) {
        super(id, startTime, timeSpent, player, kakuro);
        this.timeFinished = timeFinished;
        this.score = score;
    }

    public float getScore() {
        return this.score;
    }

    public Timestamp getTimeFinished() {
        return this.timeFinished;
    }

    public String toString() {
        return "Game Finished\n" + super.toString() + "\ntimne finished: " + timeFinished + ", score: " + score;
    }
}