package src.controllers;

import src.domain.Board;
import src.domain.WhiteCell;

import java.util.ArrayList;

public class SwappingCellQueue {
    private final Board workingBoard;

    private final int rows, columns;

    private WhiteCell[] orderedCells; // Contains all the WhiteCells in increasing order of number of notations
    private int[] startPos;           // 9 pointers to the first position with corresponding number of notations
                                      // if there is no elements of size n, then startPos[n-1] = startPos[n]
    private int endElement;           // should coincide with the size of the array
    private int firstElement;         // points to first valid element, coincides with startPos[0]

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
        startPos = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        firstElement = 0;
    }

    public boolean isEmpty() {
        return firstElement == endElement;
    }

    private int findCell(int r, int c) {
        return findCell(r, c, workingBoard.getCellNotationSize(r, c));
    }

    private int findCell(int r, int c, int notationSize) {
        // if data structure integrity is always preserved this linear search starts at the first position that has nSize
        // notations and should find the cell. If it doesn't it will return the endElement, but this shouldn't happen
        // unless notationSize is incorrect.
        if (notationSize < 0 || notationSize > 9) return endElement;

        int pos = 0;
        int end = firstElement;
        if (notationSize != 0) {
            pos = startPos[notationSize-1];
            end = notationSize == 9 ? endElement : startPos[notationSize];
        }
        while (pos < end && !workingBoard.equalsCell(r, c, orderedCells[pos])) pos++;
        return pos==end ? endElement : pos;
    }

    // returns the new notation size
    public int eraseNotationsFromCell(int r, int c, ArrayList<Integer> toErase) {
        int notSize = workingBoard.getCellNotationSize(r, c);
        int pos = findCell(r, c, notSize);
        if (pos == endElement) return notSize; // we didn't find the element, we didn't erase any notations
        boolean [] cellNotations = workingBoard.getCellNotations(r, c);
        for (int i : toErase) {
            if (cellNotations[i-1]) { // if there was such notation we erase it
                workingBoard.setCellNotation(r, c, i, false);
                cellNotations[i-1] = false;
                // swap cells in array to preserve order
                WhiteCell swap = orderedCells[startPos[notSize-1]];
                orderedCells[startPos[notSize-1]] = orderedCells[pos];
                orderedCells[pos] = swap;
                pos = startPos[notSize-1]; // update to the new position
                startPos[notSize-1] ++; // the cells with notSize are one less, so they start one position after
                notSize--; // decrement the current size of notations
                if (notSize == 0) break; // we already have removed all possible notations
            }
        }
        firstElement = startPos[0];
        return notSize;
    }

    // returns the new notation size
    public int addNotationsToCell(int r, int c, ArrayList<Integer> toAdd) {
        int notSize = workingBoard.getCellNotationSize(r, c);
        int pos = findCell(r, c, notSize);
        if (pos == endElement || toAdd.size() == 0) return notSize; // we didn't find the element, we didn't erase any notations
        boolean [] cellNotations = workingBoard.getCellNotations(r, c);
        // if somehow this cell had no notations (no possible values, may happen if we need to do rollback)
        // we need to previously find it and insert it into a valid position of size 1
        if (notSize == 0) {
            int firstAdd = toAdd.get(0);
            toAdd.remove(0);
            workingBoard.setCellNotation(r, c, firstAdd, true);
            cellNotations[firstAdd-1] = true;
            // swap with the last cell of notSize 0
            WhiteCell swap = orderedCells[firstElement-1];
            orderedCells[firstElement-1] = orderedCells[pos];
            orderedCells[pos] = swap;
            pos = firstElement-1;
            startPos[0] --;
            firstElement--;
            notSize++;
        }
        for (int i : toAdd) {
            if (!cellNotations[i-1]) { // if there was not such notation we add it
                workingBoard.setCellNotation(r, c, i, true);
                cellNotations[i-1] = true;
                // swap cells in array to preserve order
                WhiteCell swap = orderedCells[startPos[notSize]-1];
                orderedCells[startPos[notSize]-1] = orderedCells[pos];
                orderedCells[pos] = swap;
                pos = startPos[notSize]-1; // update to the new position
                startPos[notSize] --; // the cells with notSize are one more, so they start one position before
                notSize++; // increment the current size of notations
                if (notSize == 9) break; // we already have added all possible notations
            }
        }
        firstElement = startPos[0];
        return notSize;
    }

    public WhiteCell popFirstOrderedCell() {
        WhiteCell c = orderedCells[firstElement];
        for (int i = 0; i < 9; i++) {
            if (startPos[i] == firstElement) startPos[i]++;
            else break;
        }
        firstElement++;
        return c;
    }

    public void removeOrderedCell(int r, int c) { // Must be a cell in a valid position
        int notSize = workingBoard.getCellNotationSize(r, c);
        int pos = findCell(r, c, notSize);
        if (pos != endElement) {
            if (workingBoard.equalsCell(r, c, orderedCells[pos])) {
                boolean[] notations = workingBoard.getCellNotations(r, c);
                ArrayList<Integer> toRemove = new ArrayList<>();
                for (int j = 0; j < 9; j++) if (notations[j]) toRemove.add(j+1);
                eraseNotationsFromCell(r, c, toRemove);
                for (int j : toRemove)
                    workingBoard.setCellNotation(r, c, j, true);
            }
        }
    }

    public void insertOrderedCell(int r, int c) { // Must be a cell in a position previous to firstElement
        for (int i = firstElement-1; i >= 0; i--) {
            if (workingBoard.equalsCell(r, c, orderedCells[i])) {
                boolean[] notations = workingBoard.getCellNotations(r, c);
                ArrayList<Integer> toAdd = new ArrayList<>();
                for (int j = 0; j < 9; j++) {
                    if (notations[j]) toAdd.add(j+1);
                }
                workingBoard.clearCellNotations(r, c);
                addNotationsToCell(r, c, toAdd);
            }
        }
    }
}
