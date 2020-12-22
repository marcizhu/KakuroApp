package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public class GameFinished extends Game{
    private final float score;
    private final Timestamp timeFinished;
    private final boolean surrendered;

    public GameFinished(GameInProgress gip, boolean surrendered) {
        super(gip.getPlayer(), gip.getKakuro(), gip.getStartTime());
        this.surrendered = surrendered;
        this.timeFinished = new Timestamp(System.currentTimeMillis());
        this.setTimeSpent(gip.getTimeSpent());
        float tmpScore = computeScore(gip.getNumberOfHints());
        if (tmpScore < 0) tmpScore = 0;
        this.score = tmpScore;
    }

    // For deserializing
    public GameFinished(UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro, float score, Timestamp timeFinished, boolean surrendered) {
        super(id, startTime, timeSpent, player, kakuro);
        this.surrendered = surrendered;
        this.timeFinished = timeFinished;
        this.score = score;
    }

    private float computeScore(int numOfHints) {
        if (getKakuro().getDifficulty() == Difficulty.USER_MADE || surrendered || getTimeSpent() == 0) {
            return 0;
        }
        int expected_time_per_move = 0;
        int point_for_expected_completion = 0;

        switch(getKakuro().getDifficulty()) {
            case EASY:
                expected_time_per_move = 10;
                point_for_expected_completion = 10;
                break;
            case MEDIUM:
                expected_time_per_move = 15;
                point_for_expected_completion = 15;
                break;
            case HARD:
                expected_time_per_move = 20;
                point_for_expected_completion = 20;
                break;
            case EXTREME:
                expected_time_per_move = 25;
                point_for_expected_completion = 25;
                break;
        }

        int numOfWhiteCells = 0;
        for (int i = 0; i < getKakuro().getBoard().getHeight(); i++) {
            for (int j = 0; j < getKakuro().getBoard().getWidth(); j++) {
                if (getKakuro().getBoard().isWhiteCell(i, j)) numOfWhiteCells++;
            }
        }

        int expected_time_to_solve = expected_time_per_move*numOfWhiteCells - (expected_time_per_move*numOfHints)*3/4;

        float proportion = (float) expected_time_to_solve / getTimeSpent();

        return proportion * point_for_expected_completion;
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