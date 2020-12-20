package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class GameFinished extends Game{
    private float score;
    private final Timestamp timeFinished;
    private final boolean surrendered;

    public GameFinished(GameInProgress gip, boolean surrendered) {
        super(gip.getPlayer(), gip.getKakuro(), gip.getStartTime());
        this.surrendered = surrendered;
        this.timeFinished = new Timestamp(System.currentTimeMillis());
        this.setTimeSpent(gip.getTimeSpent());
        this.score = 0; // FIXME: maybe compute it here in the constructor? this.score = computeScore()
    }

    // For deserializing
    public GameFinished(UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro, float score, Timestamp timeFinished, boolean surrendered) {
        super(id, startTime, timeSpent, player, kakuro);
        this.surrendered = surrendered;
        this.timeFinished = timeFinished;
        this.score = score;
    }

    public void computeScore(int numOfHints) {
        if (getKakuro().getDifficulty() == Difficulty.USER_MADE) {
            this.score = 0;
            return;
        }
        int diff = 0;
        switch(getKakuro().getDifficulty()) {
            case EASY:
                diff = 1;
                break;
            case MEDIUM:
                diff = 2;
                break;
            case HARD:
                diff = 3;
                break;
            case EXTREME:
                diff = 4;
                break;
        }
        float d = 1000 * diff;
        float numOfWhiteCells = 0;
        float numOfBlackCells = 0;
        for (int i = 0; i < getKakuro().getBoard().getHeight(); i++) {
            for (int j = 0; j < getKakuro().getBoard().getWidth(); j++) {
                if (getKakuro().getBoard().isWhiteCell(i, j)) numOfWhiteCells++;
                else numOfBlackCells++;
            }
        }
        float k = numOfWhiteCells > 0 ? numOfBlackCells / numOfWhiteCells : 0;

        if (getTimeSpent() != 0) score = d/getTimeSpent() - numOfHints*k;

        if (score < 0) score = 0;
    }

    public float getScore() {
        return this.score;
    }

    public Timestamp getTimeFinished() {
        return this.timeFinished;
    }

    public String toString() {
        return "Game Finished\n" + super.toString() + "\ntime finished: " + timeFinished + ", score: " + score;
    }

    public boolean isSurrendered() { return this.surrendered; }
}