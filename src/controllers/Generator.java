package src.controllers;

import src.domain.*;

import java.util.ArrayList;
import java.util.Random;

public class Generator {

    private Board generatedBoard;
    private Board workingBoard;
    private SwappingCellQueue notationsQueue;

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
        int height = b.getHeight();
        int width = b.getWidth();

        if (col < width-2 && b.isBlackCell(row, col+2) && b.isWhiteCell(row, col+1)) return false;
        if (col == width-2 && b.isWhiteCell(row, col+1)) return false;
        if (col > 1 && b.isBlackCell(row, col-2) && b.isWhiteCell(row, col-1)) return false;
        if (col == 1 && b.isWhiteCell(row, col-1)) return false;

        if (row < height-2 && b.isBlackCell(row+1, col) && b.isWhiteCell(row+1, col)) return false;
        if (row == height-2 && b.isWhiteCell(row+1, col)) return false;
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
        Board b = new Board(columns, rows, new WhiteCell());
        int width = b.getWidth();
        int height = b.getHeight();

        for(int i = 0; i<height; i++) {
            for(int j = 0; j<width; j++) {
                Cell c = new WhiteCell(true);
                if (i == 0 || j == 0 || (random.nextInt() % 2 == 0 && isValidPosition(b, i, j))) {
                    // Cell will be black if we are in the first row or column or randomly with a 1/7 chance
                    c = new BlackCell();
                }
                System.out.println(b.toString());
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
        rowSums = new int[rowLineID+1];
        rowSize = new int[rowLineID+1];
        rowValuesUsed = new boolean[rowLineID+1][9];
        for (int i = 0; i < rowLineID+1; i++) { //initialize data at default values
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
        colSums = new int[colLineID+1];
        colSize = new int[colLineID+1];
        colValuesUsed = new boolean[colLineID+1][9];
        for (int i = 0; i < colLineID+1; i++) { //initialize data at default values
            colSums[i] = 0;
            colSize[i] = sizes.get(i);
            colValuesUsed[i] = new boolean[] { false, false, false, false, false, false, false, false, false };
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

        // from now on, any operation on workingBoard intended to modify the notations should be done through the gueue
        // be careful, this.workingBoard and the queue's board reference the same object, this is intentional
        // but in order to mantain integrity of the queue every value assigned should correctly erase the notations of the corresponding cell
        notationsQueue = new SwappingCellQueue(workingBoard);

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

            if (workingBoard.isEmpty(coordRow, coordCol)) { // in case a previous assignation has assigned this cell's value
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

        while (!notationsQueue.isEmpty()) {
            // then we have elements with no known value so we must do an assignation
            WhiteCell candidate = notationsQueue.popFirstOrderedCell();

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
        // generatedBoard = workingBoard; //some process of clearing values, assigning sums, etc.
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
