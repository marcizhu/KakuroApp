package src.domain;

import src.domain.Kakuro;
import src.domain.User;

import java.sql.Time;
import java.sql.Timestamp;

public abstract class Game {
    private final User player;
    private final Kakuro kakuro;
    private final float timeSpent;
    private final Timestamp startTime;

    public Game(User player, Kakuro kakuro) {
        this.player = player;
        this.kakuro = kakuro;
        this.startTime = new Timestamp(System.currentTimeMillis());
        this.timeSpent = 0;
    }

    public Kakuro getKakuro() {
        return this.kakuro;
    }

    public float getTimeSpent() {
        return this.timeSpent;
    }

    public User getPlayer() {
        return this.player;
    }

    public Timestamp getStartTime() {
        return  this.startTime;
    }

}
