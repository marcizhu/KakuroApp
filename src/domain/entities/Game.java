package src.domain.entities;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class Game {
    private final UUID id;
    private final User player;
    private final Kakuro kakuro;
    private float timeSpent;
    private final Timestamp startTime;

    public Game(User player, Kakuro kakuro) {
        this(player, kakuro, new Timestamp(System.currentTimeMillis()));
    }

    public Game(User player, Kakuro kakuro, Timestamp startTime) {
        this.id = UUID.randomUUID();
        this.player = player;
        this.kakuro = kakuro;
        this.timeSpent = 5; // FIXME: ???
        this.startTime = startTime;
    }

    // For deserializing purposes
    public Game(UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro) {
        this.id = id;
        this.player = player;
        this.kakuro = kakuro;
        this.startTime = startTime;
        this.timeSpent = timeSpent;
    }

    public void setTimeSpent(float timeSpent) {
        this.timeSpent = timeSpent;
    }

    public Kakuro getKakuro() {
        return this.kakuro;
    }

    public String getKakuroName() {
        return this.kakuro == null ? null : this.kakuro.getName();
    }

    public UUID getId () {
        return this.id;
    }

    public float getTimeSpent() {
        return this.timeSpent;
    }

    public User getPlayer() {
        return this.player;
    }

    public String getPlayerName() {
        return this.player == null ? null : this.player.getName();
    }

    public Timestamp getStartTime() {
        return  this.startTime;
    }

    public String toString () {
        return "Id: " + id.toString() + "\nPlayer: " + player.toString() + "\nKakuro: " + kakuro.toString();
    }

}
