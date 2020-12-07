package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class Game {
    private final UUID id;
    private final User player;
    private final UUID kakuroId;
    private final Kakuro kakuro; // TODO: remove this?
    private final float timeSpent;
    private final Timestamp startTime;

    public Game(User player, Kakuro kakuro) {
        this.id = UUID.randomUUID();
        this.player = player;
        this.kakuroId = kakuro.getId();
        this.kakuro = kakuro;
        this.startTime = new Timestamp(System.currentTimeMillis());
        this.timeSpent = 0;
    }

    public UUID getId () { return this.id; }

    public UUID getId () { return this.id; }

    public float getTimeSpent() {
        return this.timeSpent;
    }

    public Timestamp getStartTime() {
        return  this.startTime;
    }

}
