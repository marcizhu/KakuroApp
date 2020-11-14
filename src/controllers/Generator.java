package src.controllers;

import src.domain.*;

import java.util.ArrayList;
import java.util.Random;

public class Generator {

    private Board generatedBoard;
    private Board workingBoard;

    private int rows;
    private int columns;
    private Difficulty difficulty;

    private int[] rowSums;
    private int[] rowSize;
    private boolean[][] rowValuesUsed;
    private final int[][] rowLine; // Pointers to position at arrays of row related data

    private int[] colSums;
    private int[] colSize;
    private boolean[][] colValuesUsed;
    private final int[][] colLine; // Pointers to position at array of colValuesUsed and colSums

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
        rowLine = new int[rows][columns];
        colLine = new int[rows][columns];
        this.random = new Random();
    }

    public Generator(int rows, int columns, Difficulty difficulty, long seed) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        rowLine = new int[rows][columns];
        colLine = new int[rows][columns];
        this.random = new Random(seed);
    }

    private boolean isValidPosition(Board b, int row, int col) { // FIXME: submethod of prepareWorkingBoard
        /* TODO
        Returns false if:
            placing a black cell in position (row, col) would cause board b
            to have a row or a column with length 1
         */

        if (col < columns-2 && b.isBlackCell(row, col+2) && b.isWhiteCell(row, col+1)) return false;
        if (col == columns-2 && b.isWhiteCell(row, col+1)) return false;
        if (col > 1 && b.isBlackCell(row, col-2) && b.isWhiteCell(row, col-1)) return false;
        if (col == 1 && b.isWhiteCell(row, col-1)) return false;

        if (row < rows-2 && b.isBlackCell(row+1, col) && b.isWhiteCell(row+1, col)) return false;
        if (row == rows-2 && b.isWhiteCell(row+1, col)) return false;
        if (row > 1 && b.isBlackCell(row-2, col) && b.isWhiteCell(row-1, col)) return false;
        if (row == 1 && b.isWhiteCell(row-1, col)) return false;

        return true;
    }

    public Board prepareWorkingBoard() { //FIXME: PRIVATE!!
        /* TODO: Generate the black cells on a board of size columns x rows for a given difficulty
        *   - The orderedCells size is rows*columns - numberOfBlackCells, it only contains WhiteCells
        *   - startPos is an array of "pointers" of size 9, startingPos[x-1] has the position in orderedCells
        *       where there is the first WhiteCell with x anotated values
        * */
        Board b = new Board(columns, rows);
        int width = b.getWidth();
        int height = b.getHeight();

        for(int i = 0; i<height; i++) {
            for(int j = 0; j<width; j++) {
                Cell c = new WhiteCell(true);
                if (i == 0 || j == 0 || (random.nextInt() % 5 == 0 && isValidPosition(b, i, j))) {
                    // Cell will be black if we are in the first row or column or randomly with a 1/7 chance
                    c = new BlackCell();
                }
                b.setCell(c, i, j);
            }
        }

        // Traverse the board once and fix all rows and columns of length > 9
        for(int i = 0; i<height; i++) {
            for(int j = 0; j<width; j++) {
                if (b.isBlackCell(i, j)) continue;

                int rowLength = 1;
                int pos = j+1;
                while (pos < width && b.isWhiteCell(i, pos)) {
                    rowLength++;
                    pos++;
                }
                pos = j-1;
                while (j > 0 && b.isWhiteCell(i, pos)) {
                    rowLength++;
                    pos--;
                }

                int colLength = 1;
                pos = i+1;
                while (pos < height && b.isWhiteCell(pos, j)) {
                    colLength++;
                    pos++;
                }
                pos = i-1;
                while (j > 0 && b.isWhiteCell(pos, j)) {
                    colLength++;
                    pos--;
                }

                if(rowLength > 9 || colLength > 9) {
                    //TODO
                }
            }
        }

        return b;
    }

    private void preprocessRows() {
        int size = 0;
        int rowLineID = 0;
        ArrayList<Integer> sizes = new ArrayList<>();
        for(int i = 1; i < rows; i++) {
            for (int j = 1; j < columns; j++) {
                if(workingBoard.isBlackCell(i, j)) {
                    if (workingBoard.isWhiteCell(i, j-1)) {// there is a row before the black cell
                        sizes.add(size);
                        size = 0;
                        rowLineID++; //prepare for next rowLine
                    }
                    if (j+1 < columns && workingBoard.isWhiteCell(i, j+1)) {
                        rowLine[i][j] = rowLineID; //black cell is responsible for next rowLine
                    } else {
                        rowLine[i][j] = -1; //black cell is not responsible for any rowLine
                    }
                } else {
                    // assign rowLineID to this member of current rowLine
                    rowLine[i][j] = rowLineID;
                    size++;
                }
            }
            if (size > 0) { //last rowLine in row if we have seen whiteCells
                sizes.add(size);
                size = 0;
                rowLineID++; //prepare for next rowLine
            }
        }
        rowSums = new int[rowLineID];
        rowSize = new int[rowLineID];
        rowValuesUsed = new boolean[rowLineID][9];
        for (int i = 0; i < rowLineID; i++) { //initialize data at default values
            rowSums[i] = 0;
            rowSize[i] = sizes.get(i);
            rowValuesUsed[i] = new boolean[] { false, false, false, false, false, false, false, false, false };
        }
    }

    private void preprocessCols() {
        int size = 0;
        int colLineID = 0;
        ArrayList<Integer> sizes = new ArrayList<>();
        for(int i = 1; i < columns; i++) {
            for (int j = 1; j < rows; j++) {
                if(workingBoard.isBlackCell(i, j)) {
                    if (workingBoard.isWhiteCell(j-1, i)) {// there is a col before the black cell
                        sizes.add(size);
                        size = 0;
                        colLineID++; //prepare for next colLine
                    }
                    if (j+1 < rows && workingBoard.isWhiteCell(j+1, i)) {
                        colLine[j][i] = colLineID; //black cell is responsible for next colLine
                    } else {
                        colLine[j][i] = -1; //black cell is not responsible for any colLine
                    }
                } else {
                    // assign colLineID to this member of current colLine
                    colLine[j][i] = colLineID;
                    size++;
                }
            }
            if (size > 0) { //last colLine in col if we have seen whiteCells
                sizes.add(size);
                size = 0;
                colLineID++; //prepare for next colLine
            }
        }
        colSums = new int[colLineID];
        colSize = new int[colLineID];
        colValuesUsed = new boolean[colLineID][9];
        for (int i = 0; i < colLineID; i++) { //initialize data at default values
            colSums[i] = 0;
            colSize[i] = sizes.get(i);
            colValuesUsed[i] = new boolean[] { false, false, false, false, false, false, false, false, false };
        }
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
        boolean success = false;



        return success;
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
        preprocessRows();
        preprocessCols();

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

            if (workingBoard.getValue(coordRow, coordCol) == 0) { // in case a previous assignation has assigned this cell's value
                ArrayList<int[]> uniqueCrossValues = KakuroConstants.INSTANCE.getUniqueCrossValues(rowSize[rowLine[coordRow][coordCol]], colSize[colLine[coordRow][coordCol]], difficulty); // returns [] of {rowSum, colSum, valueInCommon}
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
