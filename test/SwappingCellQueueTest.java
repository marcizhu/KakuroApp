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
        Board board = new Board(9, 9, new WhiteCell(true));
        SwappingCellQueue scq = new SwappingCellQueue(board);

        scq.removeOrderedCell(2, 2);
        scq.insertOrderedCell(2, 2);
        scq.eraseNotationsFromCell(2, 2, new ArrayList<Integer>(Arrays.asList(1, 2, 3)));

        assertTrue(Arrays.equals(new boolean[]{false, false, false, true, true, true, true, true, true}, board.getCellNotations(2, 2)));
    }
}
