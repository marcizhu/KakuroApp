package src.controllers;

import src.domain.Board;
import src.domain.WhiteCell;
import src.domain.Difficulty;

import java.util.ArrayList;
import java.util.Random;

public class Generator {

    private Board generatedBoard;
    private Board workingBoard;

    private int rows;
    private int columns;
    private Difficulty difficulty;

    private final int[][] rowSize; ///< The size of the row for a cell
    private final int[][] colSize; ///< The size of the column for a cell

    private Random random;

    private WhiteCell[] orderedCells; // Contains all the WhiteCells in increasing order of number of anotations
    private int[] startPos;           // 9 pointers to the first position with corresponding number of anotations
                                      // if there is no elements of size n, then startPos[n-1] = startPos[n]
    private int endElement;           // should coincide with the size of the array
    private int firstElement;         // points to first element not yet used, coincides with startPos[0]

    public Generator(int rows, int columns, Difficulty difficulty) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        rowSize = new int[rows][columns];
        colSize = new int[rows][columns];
        this.random = new Random();
    }

    public Generator(int rows, int columns, Difficulty difficulty, long seed) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        rowSize = new int[rows][columns];
        colSize = new int[rows][columns];
        this.random = new Random(seed);
    }

    private Board prepareWorkingBoard() {
        /* TODO: Generate the black cells on a board of size columns x rows for a given difficulty
        *   - The orderedCells size is rows*columns - numberOfBlackCells, it only contains WhiteCells
        *   - startPos is an array of "pointers" of size 9, startingPos[x-1] has the position in orderedCells
        *       where there is the first WhiteCell with x anotated values
        * */
        return new Board();
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
    private int eraseNotationsFromCell(int r, int c, ArrayList<Integer> toErase) {
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
    private int addNotationsToCell(int r, int c, ArrayList<Integer> toAdd) {
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

    private WhiteCell popFirstOrderedCell() {
        WhiteCell c = orderedCells[firstElement];
        for (int i = 0; i < 9; i++) {
            if (startPos[i] == firstElement) startPos[i]++;
            else break;
        }
        firstElement++;
        return c;
    }

    private void removeOrderedCell(int r, int c) { // Must be a cell in a valid position
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

    private void insertOrderedCell(int r, int c) { // Must be a cell in a position previous to firstElement
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


    // TODO: THE THREE RECURSIVE ASSIGNATION METHODS BELOW COULD POSSIBLY FAIL AN ASSIGNATION SO THEY MUST RETURN A
    //  BOOLEAN INDICATING IF THEY WERE SUCCESSFUL, ALSO, IF THEY WEREN'T THEY SHOULD BE ABLE TO ROLLBACK ANY CHANGES
    //  TO VALUES, SUMS AND NOTATIONS THAT THEY HAVE CAUSED, WHICH MEANS THAT IN THE CASE THAT AN ASSIGNATION
    //  PROVOQUES MORE THAN ONE OTHER ASSIGNATION (RECURSIVELY), IF ANY OF THEM FAIL (AS THEY NEED TO SUCCEED TO MAKE
    //  THE CURRENT ASSIGNATION SUCCESSFUL), ALL OF THEM SHOULD ROLLBACK AND THE CURRENT ONE SHOULD PRESERVE THE VALUE,
    //  NOTATIONS, SUMS ETC. THAT IT HAD IN THE BEGINNING AND RETURN FALSE SO THAT THE RESPONSIBLE FOR ITS CALL
    //  KNOWS IT HAS FAILED.
    private boolean rowSumAssignation(int r, int c, int value) {
        // TODO: should update the row sum for a given coordinates to the value
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        return false;
    }

    private boolean colSumAssignation(int r, int c, int value) {
        // TODO: should update the col sum for a given coordinates to the value
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        return false;
    }

    private boolean cellValueAssignation(int r, int c, int value) {
        // TODO: should update the assignation for that cell, remove the notations, update the
        //  orderedCells data structure and its pointers, etc.
        //  Could call other assignations recursively
        return false;
    }

    public void generate() {
        // Fill the black cells in an empty board, all white cells should have all 9 values in anotations, should fill
        // the data structure to keep white cells ordered increasingly by number of anotations.
        workingBoard = prepareWorkingBoard();

        // TODO: probably should preprocess row and column spaces

        // Select some random white cells to begin the assignations, depending on difficulty
        int numOfStartingPoints = 0;
        switch(difficulty) {
            case EASY:
                numOfStartingPoints += 2; // EASY will have 6 starting points
            case MEDIUM:
                numOfStartingPoints += 2; // MEDIUM will have 4 starting points
            case HARD:
                numOfStartingPoints ++;   // HARD will have 2 starting points
            case EXTREME:
                numOfStartingPoints ++;   // EXTREME will have 1 starting point
        }
        for (int start = 0; start < numOfStartingPoints; start++) {
            int coordRow = random.nextInt(rows);
            int coordCol = random.nextInt(columns);

            if (workingBoard.getValue(coordRow, coordCol) == 0) {
                int rowSpace = 0, colSpace = 0; // TODO: this is where we assign according to precomputed space values
                ArrayList<int[]> uniqueCrossValues = KakuroConstants.INSTANCE.getUniqueCrossValues(rowSpace, colSpace, difficulty); // returns [] of {rowSum, colSum, valueInCommon}
                for (int[] uniqueValue : uniqueCrossValues) {
                    // TODO: assign uniqueValue[0] to the row sum, uniqueValue[1] to de column sum
                    //  and uniqueValue[2] to de WhiteCell value in [coordRow][coordCol]
                    //  these assignments will take care to modify the notations and call other
                    //  assignments recursively as well as change the pointers to orderedCells accordingly
                    //  , if an assignment is successful we shouldn't call the next ones;
                }
            }
        }

        // At this point we've had a number of successful starting point assignments, now we assign the WhiteCell
        // that has the least notations (possible values) in a way that makes it non ambiguous

        while (firstElement < endElement) {
            // then we have elements with no known value so we must do an assignation
            WhiteCell candidate = popFirstOrderedCell();

            // if one or both of the sums are not assigned, we should choose the value in
            // its notations that given the current values in the row and column there is a unique value for a
            // certain sum assignation

            // if both sums are already assigned, then that means there can still go more than one option in this
            // cell, so we choose and the repercutions of the choice hopefully will make it a unique choice in a
            // posterior sum assignment that affects other positions in the same row/col.
        }

        // when we get out of the while loop we should have a filled board generated,
        // maybe we want to send it to the solver to check if it's unique or not or check for permutations, etc.
        // then:
        generatedBoard = workingBoard;
    }
}



/// FOR DEBUGGING PURPOSES ///

    /*public void testDataStructure() {
        // SETUP OF ALL WHITE CELLS
        this.rows = 4;
        this.columns = 4;
        this.random = new Random();

        endElement = rows*columns;
        orderedCells = new WhiteCell[endElement];
        startPos = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        firstElement = 0;

        workingBoard = new Board(rows,columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                WhiteCell wc = new WhiteCell(true);
                workingBoard.setCell(wc, i, j);
                orderedCells[i*columns+j] = wc;
            }
        }

        // ADDING, REMOVING, etc. TESTING
        ArrayList<Integer> v = new ArrayList<>();
        v.add(2);
        v.add(3);
        eraseNotationsFromCell(2, 2, v);
        v.add(4);
        eraseNotationsFromCell(2, 3, v);
        v.add(5);
        eraseNotationsFromCell(3, 3, v);
        v.add(1);
        v.add(6);
        v.add(7);
        v.add(8);
        eraseNotationsFromCell(0, 0, v);
        v.add(9);
        eraseNotationsFromCell(1, 0, v);
        writeOrderedCells();

        ArrayList<Integer> b = new ArrayList<>();
        b.add(2);
        b.add(3);
        addNotationsToCell(1, 0, b);
        writeOrderedCells();

        System.out.println("Poping ... ");
        popFirstOrderedCell();
        writeOrderedCells();

        System.out.println("Removing ... ");
        removeOrderedCell(3, 2);
        writeOrderedCells();

        System.out.println("Inserting ... ");
        insertOrderedCell(3, 2);
        writeOrderedCells();
    }

    private void writeOrderedCells() {
        for (int i = 0; i < endElement; i++) {
            System.out.println("[ " + i + " : " + orderedCells[i].getNotationSize() + " ]");
        }
        System.out.println("Pointers: firstElement: " + firstElement);
        for (int i = 0; i < 9; i++) {
            System.out.print("{ " + (i+1) + " :: " + startPos[i] + " }, ");
        }
        System.out.println();
    }*/
