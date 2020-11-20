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
        scq.addNotationsToCell(ROWS/2, COLUMNS/3, new ArrayList<>(Arrays.asList(1, 7, 2, 3)));
        scq.removeOrderedCell(ROWS/2, COLUMNS/3);
        scq.insertOrderedCell(ROWS/2, COLUMNS/3);
        //should remove the 3 values and should have 4 notations total
        scq.eraseNotationsFromCell(ROWS/2, COLUMNS/3, new ArrayList<>(Arrays.asList(1, 2, 3)));

        assertTrue(Arrays.equals(new boolean[]{ false, false, false, true, false, true, true, true, false }, board.getCellNotations(ROWS/2, COLUMNS/3)));
    }

    @Test
    public void hidingBehaviourTest() {
        final int ROWS = 3, COLUMNS = 3;
        Board board = new Board(COLUMNS, ROWS, new WhiteCell(true));
        SwappingCellQueue scq = new SwappingCellQueue(board);

        int offset = ROWS*COLUMNS/9;
        int lastAdded = 1;
        ArrayList<Integer> toErase = new ArrayList<>();
        toErase.add(1);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i * ROWS + j) % offset == 0) {
                    lastAdded++;
                    toErase.add(lastAdded);
                }
                scq.eraseNotationsFromCell(i, j, toErase);
            }
        }

        scq.addNotationsToCell(0, 0, new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        scq.addNotationsToCell(ROWS-1, COLUMNS-1, new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));

        scq.removeOrderedCell(0, 0);
        scq.hideElement(ROWS-1, COLUMNS-1);

        assertTrue(!scq.isHiding(0, 0));
        assertTrue(scq.isHiding(ROWS-1, COLUMNS-1));

        scq.eraseNotationsFromCell(0, 0, new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        scq.eraseNotationsFromCell(ROWS-1, COLUMNS-1, new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));

        assertTrue(board.getCellNotationSize(0, 0) == 9);
        assertTrue(board.getCellNotationSize(ROWS-1, COLUMNS-1) == 0);

        scq.insertOrderedCell(0, 0);
        scq.eraseNotationsFromCell(0, 0, new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5)));
        assertTrue(Arrays.equals(new boolean[]{ false, false, false, false, false, true, true, true, true }, board.getCellNotations(0, 0)));
    }

    @Test
    public void increasingOrderOfNotationsTest() {
        final int ROWS = 3, COLUMNS = 3;
        Board board = new Board(COLUMNS, ROWS, new WhiteCell(true));
        SwappingCellQueue scq = new SwappingCellQueue(board);

        int offset = ROWS*COLUMNS/9;
        int lastAdded = 1;
        ArrayList<Integer> toErase = new ArrayList<>();
        toErase.add(1);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i * ROWS + j) % offset == 0) {
                    lastAdded++;
                    toErase.add(lastAdded);
                }
                scq.eraseNotationsFromCell(i, j, toErase);
            }
        }

        boolean orderBroken = false;
        int lastSeenSize = 0;
        while(!orderBroken && !scq.isEmpty()) {
            int currentSize = scq.getFirstElement().getNotationSize();
            if (currentSize < lastSeenSize) orderBroken = true;
            scq.hideFirstElement();
        }

        assertTrue(!orderBroken);
    }
}
