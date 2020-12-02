package src.domain.algorithms.helpers;

import src.domain.entities.Board;
import src.domain.entities.WhiteCell;

import java.util.ArrayList;

/**
 * Swapping Cell queue.
 *
 * @version 0.1.0 (20/11/2020)
 */

public class SwappingCellQueue {
    private final Board workingBoard;

    private final int rows, columns;

    private WhiteCell[] orderedCells; // Contains all the WhiteCells in increasing order of number of notations
    private int[] startPos;           // 1 pointer to hiding cells and 9 pointers to the first position with corresponding number of notations
                                      // if there is no elements of size n, then startPos[n-1] = startPos[n]
                                      // any element in a position before startPos[0] is in an invalid position.
    private int endElement;           // should coincide with the size of the array
    private int firstElement;         // points to first valid element, coincides with startPos[1], any element
                                      // before it and after startPos[0] is hiding

    /**
     * Constructor
     * @param b Board to work with
     */
    public SwappingCellQueue(Board b) {
        workingBoard = b;
        rows = b.getHeight();
        columns = b.getWidth();
        endElement = rows*columns;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isBlackCell(i,j)) endElement--;
            }
        }
        // we fill orderedCells assuming an empty board with all WhiteCells with 9 notations
        orderedCells = new WhiteCell[endElement];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isWhiteCell(i,j)) {
                    orderedCells[idx] = (WhiteCell) workingBoard.getCell(i,j);
                    idx++;
                }
            }
        }
        startPos = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        firstElement = 0;
    }

    /**
     * Check if queue is empty
     * @return whether the queue is empty or not
     */
    public boolean isEmpty() {
        return firstElement == endElement;
    }

    private int findCell(int r, int c, int notationSize) {
        // if data structure integrity is always preserved this linear search starts at the first position that has nSize
        // notations and should find the cell. If it doesn't it will return the endElement, but this shouldn't happen
        // unless notationSize is incorrect or the cell has been removed. It should find hiding cells.
        if (notationSize < -1 || notationSize > 9) return endElement;

        int pos = 0;
        int end = firstElement;
        if (notationSize != 0) {
            if (notationSize == -1) { // search in hiding positions
                pos = startPos[0];
            } else {
                pos = startPos[notationSize];
                end = notationSize == 9 ? endElement : startPos[notationSize+1];
            }
        }
        while (pos < end && !workingBoard.equalsCell(r, c, orderedCells[pos])) pos++;
        if (pos != end) return pos;
        if (notationSize > 0) return findCell(r, c, -1);
        return endElement;
    }

    // returns the new notation size
    public int eraseNotationsFromCell(int r, int c, int toErase) {
        int notSize = workingBoard.getCellNotationSize(r, c);
        int pos = findCell(r, c, notSize);
        if (pos == endElement) return notSize; // we didn't find the element, we didn't erase any notations
        if (pos < firstElement) { // it was hiding, we re-insert it where it should be before subtracting
            insertOrderedCell(r, c);
            pos = findCell(r, c, notSize);
        }
        int cellNotations = workingBoard.getCellNotations(r, c);
        for (int i = 1; i <= 9; i++) {
            if ((toErase & (1<<(i-1))) == 0) continue;
            if ((cellNotations & (1<<(i-1))) != 0) { // if there was such notation we erase it
                workingBoard.setCellNotation(r, c, i, false);
                cellNotations &= ~(1<<(i-1));
                // swap cells in array to preserve order
                WhiteCell swap = orderedCells[startPos[notSize]];
                orderedCells[startPos[notSize]] = orderedCells[pos];
                orderedCells[pos] = swap;
                pos = startPos[notSize]; // update to the new position
                startPos[notSize] ++; // the cells with notSize are one less, so they start one position after
                notSize--; // decrement the current size of notations
                if (notSize == 0) { // we already have removed all possible notations we send it to invalid region
                    swap = orderedCells[startPos[0]];
                    orderedCells[startPos[0]] = orderedCells[pos];
                    orderedCells[pos] = swap;
                    startPos[0]++;
                    break;
                }
            }
        }
        firstElement = startPos[1];
        return notSize;
    }

    // returns the new notation size
    public int addNotationsToCell(int r, int c, int toAdd) {
        int notSize = workingBoard.getCellNotationSize(r, c);
        int pos = findCell(r, c, notSize);
        if (pos == endElement || toAdd == 0) return notSize; // we didn't find the element, we didn't erase any notations
        int cellNotations = workingBoard.getCellNotations(r, c);
        // if somehow this cell had no notations (no possible values, may happen if we need to do rollback)
        // we need to previously find it and insert it into a valid position of size 1
        if (notSize == 0 && startPos[0]>0) {
            // swap with the last invalid cell, enter the hiding region
            WhiteCell swap = orderedCells[startPos[0]-1];
            orderedCells[startPos[0]-1] = orderedCells[pos];
            orderedCells[pos] = swap;
            pos = startPos[0]-1;
            startPos[0]--;
        }
        for (int i = 1; i <= 9; i++) {
            if ((toAdd & (1<<(i-1))) == 0) continue;
            if ((cellNotations & (1<<(i-1))) == 0) { // if there was not such notation we add it
                workingBoard.setCellNotation(r, c, i, true);
                cellNotations |= 1 << (i-1);
                // swap cells in array to preserve order
                WhiteCell swap = orderedCells[startPos[notSize+1]-1];
                orderedCells[startPos[notSize+1]-1] = orderedCells[pos];
                orderedCells[pos] = swap;
                pos = startPos[notSize+1]-1; // update to the new position
                startPos[notSize+1] --; // the cells with notSize are one more, so they start one position before
                notSize++; // increment the current size of notations
                if (notSize == 9) break; // we already have added all possible notations
            }
        }
        firstElement = startPos[1];
        return notSize;
    }

    public WhiteCell getFirstElement() {
        return orderedCells[firstElement];
    }

    public void hideFirstElement() {
        for (int i = 1; i <= 9; i++) {
            if (startPos[i] == firstElement) startPos[i]++;
            else break;
        }
        firstElement++;
    }

    public void hideElement(int r, int c) {
        removeOrderedCell(r, c);
        int pos = findCell(r, c, 0);
        if (pos >= startPos[0]) return;

        WhiteCell swap = orderedCells[startPos[0]-1];
        orderedCells[startPos[0]-1] = orderedCells[pos];
        orderedCells[pos] = swap;
        startPos[0]--;
    }

    public boolean isHiding(int r, int c) {
        return findCell(r, c, -1) != endElement;
    }

    public void removeOrderedCell(int r, int c) { // Must be a cell in a valid position
        int notSize = workingBoard.getCellNotationSize(r, c);
        int pos = findCell(r, c, notSize);
        if (pos != endElement) {
            if (workingBoard.equalsCell(r, c, orderedCells[pos])) {
                int notations = workingBoard.getCellNotations(r, c);
                eraseNotationsFromCell(r, c, notations);
                for (int i = 0; i < 9; i++)
                    workingBoard.setCellNotation(r, c, i+1, (notations & (1<<i)) != 0);
            }
        }
    }

    public void insertOrderedCell(int r, int c) { // Must be a cell in a position previous to firstElement
        boolean elementFound = false;
        for (int i = firstElement-1; !elementFound &&  i >= 0; i--) {
            if (workingBoard.equalsCell(r, c, orderedCells[i])) {
                elementFound = true;
                int notations = workingBoard.getCellNotations(r, c);
                workingBoard.clearCellNotations(r, c);
                addNotationsToCell(r, c, notations);
            }
        }
    }
}
