package src.domain;

public class GameInProgress extends Game{
    private Board board;

    public GameInProgress(User player, Kakuro kakuro, Board board) {
        super(player, kakuro);
        this.board = board;
    }

    public Board getBoard() {
        return this.board;
    }
}
