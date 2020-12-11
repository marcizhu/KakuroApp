package src.domain.entities;

import src.utils.Pair;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class GameInProgress extends Game{
    private Board board;
    private ArrayList<Movement> movements; // Should always be ordered from idx 1 to n

    public GameInProgress(User player, Kakuro kakuro) {
        super(player, kakuro);
        this.board = new Board(kakuro.getBoard());
        movements = new ArrayList<>();
    }

    public GameInProgress(User player, Kakuro kakuro, Board board) {
        super(player, kakuro);
        this.board = board;
        movements = new ArrayList<>();
    }

    public GameInProgress(UUID id, Timestamp startTime, float timeSpent, User player, Kakuro kakuro, Board board, ArrayList<Movement> movements) {
        super(id, startTime, timeSpent, player, kakuro);
        this.board = board;
        this.movements = movements;
    }

    public UUID getBoardId() {
        return this.board == null ? null : this.board.getId();
    }

    public void insertMovement(Movement move) {
        int idx = move.getIndex();
        // If there is any movement after this one we remove it.
        boolean rebuildFromMovements = false;
        if (idx <= movements.size()) {
            rebuildFromMovements = true;
            for (int i = movements.size()-1; i >= idx-1 && i > 0; i--) {
                movements.remove(i);
            }
        }
        movements.add(idx-1, move);
        if (rebuildFromMovements) {
            rebuildBoardFromMovements();
        } else {
            Pair<Integer, Integer> coord = move.getCoordinates();
            if (move.getNext() == 0) board.clearCellValue(coord.first, coord.second);
            else board.setCellValue(coord.first, coord.second, move.getNext());
        }
    }

    private void rebuildBoardFromMovements() {
        this.board = new Board(super.getKakuro().getBoard());
        for (Movement move : movements) {
            Pair<Integer, Integer> coord = move.getCoordinates();
            if (move.getNext() == 0) board.clearCellValue(coord.first, coord.second);
            else board.setCellValue(coord.first, coord.second, move.getNext());
        }
    }

    public ArrayList<Movement> getMovements() {
        return movements;
    }

    public Board getBoard() {
        return this.board;
    }
}
