package test;

import org.junit.jupiter.api.Test;
import src.controllers.SwappingCellQueue;
import src.domain.Board;
import src.domain.WhiteCell;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwappingCellQueueTest {
    @Test
    public void notationOperationsTest() {
        final int ROWS = 9, COLUMNS = 9;
        Board board = new Board(COLUMNS, ROWS, new WhiteCell(true));
        SwappingCellQueue scq = new SwappingCellQueue(board);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (i < ROWS/3) {
                    // should have 3 notations left
                    scq.eraseNotationsFromCell(i, j, new ArrayList<>(Arrays.asList( 4, 5, 2, 6, 8, 1)));
                } else if (i < 2*ROWS/3) {
                    // should have 5 notations left
                    scq.eraseNotationsFromCell(i, j, new ArrayList<>(Arrays.asList( 5, 3, 7, 3, 9, 9, 7 ))); //only erases values once?
                } else {
                    // should have 7 notations left
                    scq.eraseNotationsFromCell(i, j, new ArrayList<>(Arrays.asList( 2, 3 )));
                }
            }
        }
        //should only add 3, 7 (which we removed) and should have 7 notations total
        scq.addNotationsToCell(ROWS/2, COLUMNS/3, new ArrayList<Integer>(Arrays.asList(1, 7, 2, 3)));
        scq.removeOrderedCell(ROWS/2, COLUMNS/3);
        scq.insertOrderedCell(ROWS/2, COLUMNS/3);
        //should remove the 3 values and should have 4 notations total
        scq.eraseNotationsFromCell(ROWS/2, COLUMNS/3, new ArrayList<Integer>(Arrays.asList(1, 2, 3)));

        assertTrue(Arrays.equals(new boolean[]{ false, false, false, true, false, true, true, true, false }, board.getCellNotations(ROWS/2, COLUMNS/3)));
    }
}
