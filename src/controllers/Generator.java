package src.controllers;

import src.domain.*;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Map;
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
    private Coordinates[] firstRowCoord;
    private final int[][] rowLine; // Pointers to position at arrays of row related data
    private int rowLineSize;

    private int[] colSums;
    private int[] colSize;
    private boolean[][] colValuesUsed;
    private Coordinates[] firstColCoord;
    private final int[][] colLine; // Pointers to position at array of colValuesUsed and colSums
    private int colLineSize;

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

        if (row < height-2 && b.isBlackCell(row+2, col) && b.isWhiteCell(row+1, col)) return false;
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
        Board b = new Board(columns, rows, new WhiteCell(true));
        int width = b.getWidth();
        int height = b.getHeight();

        // Chance of a white cell turning black
        int diff; //TODO: tweak parameters

        switch (difficulty) {
            case EASY:
                diff = 80;
                break;
            case MEDIUM:
                diff = 60;
                break;
            case HARD:
                diff = 45;
                break;
            case EXTREME:
                diff = 20;
                break;
            default:
                diff = 50;
        }

        for(int i = 0; i<height; i++) {
            for(int j = 0; j<width; j++) {
                Cell c = new WhiteCell(true);
                if (i == 0 || j == 0 || (Math.abs(random.nextInt(100)) < diff && isValidPosition(b, i, j))) {
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

                int rowStart, rowEnd;
                int pos = j+1;
                while (pos < width && b.isWhiteCell(i, pos)) {
                    pos++;
                }
                rowEnd = pos-1;
                pos = j-1;
                while (j > 0 && b.isWhiteCell(i, pos)) {
                    pos--;
                }
                rowStart = pos+1;

                // FIXME: this can result in columns of size 1!!! :(
                while (rowEnd - rowStart + 1 > 9) {
                    for (int p = rowStart + 9; p >= rowStart; p--) {
                        if (isValidPosition(b, i, p) || p == rowStart) {
                            b.setCell(new BlackCell(), i, p);
                            rowStart = p+1;
                            break;
                        }
                    }
                }

                // Fix columns with len  > 9
                int colStart, colEnd;
                pos = i+1;
                while (pos < height && b.isWhiteCell(pos, j)) {
                    pos++;
                }
                colEnd = pos-1;
                pos = i-1;
                while (j > 0 && b.isWhiteCell(pos, j)) {
                    pos--;
                }
                colStart = pos+1;

                while (colEnd - colStart + 1 > 9) {
                    for (int p = colStart + 9; p >= colStart; p--) {
                        if (isValidPosition(b, p, j) || p == colStart) {
                            b.setCell(new BlackCell(), p, j);
                            colStart = p+1;
                            break;
                        }
                    }
                }
            }
        }

        return b;
    }

    private void preprocessRows() {
        int size = 0;
        int rowLineID = 0;
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Coordinates> coord = new ArrayList<>();
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(workingBoard.isBlackCell(i, j)) {
                    if (j-1 >= 0 && workingBoard.isWhiteCell(i, j-1)) {// there is a row before the black cell
                        sizes.add(size);
                        size = 0;
                        rowLineID++; //prepare for next rowLine
                    }
                    if (j+1 < columns && workingBoard.isWhiteCell(i, j+1)) {
                        rowLine[i][j] = rowLineID; //black cell is responsible for next rowLine
                        coord.add(new Coordinates(i, j+1));
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
        rowLineSize = rowLineID;
        rowSums = new int[rowLineSize];
        rowSize = new int[rowLineSize];
        rowValuesUsed = new boolean[rowLineSize][9];
        firstRowCoord = new Coordinates[rowLineSize];
        for (int i = 0; i < rowLineSize; i++) { //initialize data at default values
            rowSums[i] = 0;
            rowSize[i] = sizes.get(i);
            rowValuesUsed[i] = new boolean[] { false, false, false, false, false, false, false, false, false };
            firstRowCoord[i] = coord.get(i);
        }
    }

    private void preprocessCols() {
        int size = 0;
        int colLineID = 0;
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Coordinates> coord = new ArrayList<>();
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if(workingBoard.isBlackCell(j, i)) {
                    if (j-1 >= 0 && workingBoard.isWhiteCell(j-1, i)) {// there is a col before the black cell
                        sizes.add(size);
                        size = 0;
                        colLineID++; //prepare for next colLine
                    }
                    if (j+1 < rows && workingBoard.isWhiteCell(j+1, i)) {
                        colLine[j][i] = colLineID; //black cell is responsible for next colLine
                        coord.add(new Coordinates(j+1, i));
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
        colLineSize = colLineID;
        colSums = new int[colLineSize];
        colSize = new int[colLineSize];
        colValuesUsed = new boolean[colLineSize][9];
        firstColCoord = new Coordinates[colLineSize];
        for (int i = 0; i < colLineSize; i++) { //initialize data at default values
            colSums[i] = 0;
            colSize[i] = sizes.get(i);
            colValuesUsed[i] = new boolean[] { false, false, false, false, false, false, false, false, false };
            firstColCoord[i] = coord.get(i);
        }
    }

    // THE THREE RECURSIVE ASSIGNATION METHODS BELOW COULD POSSIBLY FAIL AN ASSIGNATION SO THEY MUST RETURN A
    //  BOOLEAN INDICATING IF THEY WERE SUCCESSFUL, ALSO, IF THEY WEREN'T THE RESPONSIBLE FOR THE CALL
    //  SHOULD BE ABLE TO ROLLBACK ANY CHANGES TO VALUES, SUMS AND NOTATIONS THAT THEY HAVE CAUSED, WHICH MEANS
    //  THAT IN THE CASE THAT AN ASSIGNATION PROVOQUES MORE THAN ONE OTHER ASSIGNATION (RECURSIVELY),
    //  IF ANY OF THEM FAIL (AS THEY ALL NEED TO SUCCEED TO MAKE THE FIRST ASSIGNATION SUCCESSFUL),
    //  ALL OF THEM SHOULD ROLLBACK AND THE CURRENT ONE SHOULD PRESERVE THE VALUE, NOTATIONS, SUMS ETC. THAT
    //  IT HAD IN THE BEGINNING AND RETURN FALSE SO THAT THE RESPONSIBLE FOR ITS CALL
    //  KNOWS IT HAS FAILED AND CAN DO THE ROLLBACK.
    // RollBack structures are to add any change we do, we don't do rollback in these functions
    // because we only change absolutely necessary cases that depend only on the first assignation call,
    // thus the responsible for the rollback is the first caller
    private boolean rowSumAssignation(int r, int c, int value, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // FIXME: DEBUG System.out.println("rowSumAssignation for coord " + r + "," + c + " , value: " + value);
        // Should update the row sum for a given coordinates to the value and add row to rollback
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        int rowID = rowLine[r][c];
        if (rowSums[rowID] != 0) {
            // FIXME: DEBUGGING PURPOSES abortion reason
            // System.out.println("ABORTING: row has already a value assigned at " + r + ", " + c + "  of: " + rowSums[rowID]);
            return false; //already has a sum value assigned
        }
        rowSums[rowID] = value;
        rowSumRollBack.add(rowID);
        return updateRowNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
    }

    private boolean colSumAssignation(int r, int c, int value, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // FIXME: DEBUG System.out.println("colSumAssignation for coord " + r + "," + c + " , value: " + value);
        // Should update the col sum for a given coordinates to the value, and add column to rollback
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        int colID = colLine[r][c];
        if (colSums[colID] != 0) {
            // FIXME: DEBUGGING PURPOSES abortion reason
            // System.out.println("ABORTING: column has already a value assigned at " + r + ", " + c + "  of: " + colSums[colID]);
            return false; //already has a sum value assigned
        }
        colSums[colID] = value;
        colSumRollBack.add(colID);
        return updateColNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
    }

    private boolean cellValueAssignation(int r, int c, int value, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // FIXME: DEBUG System.out.println("cellValueAssignation for coord " + r + "," + c + " , value: " + value);
        // Should update the assignation for that cell, set the value, update the orderedCells data structure
        //  and its pointers with removeOrderedCell, update valuesUsed for row and col, and add it to rollback.
        //  Could call other assignations recursively
        int rowID = rowLine[r][c];
        int colID = colLine[r][c];
        if (rowValuesUsed[rowID][value-1] ||  colValuesUsed[colID][value-1] || !workingBoard.isEmpty(r, c)){
            if (rowValuesUsed[rowID][value-1] && colValuesUsed[colID][value-1] && workingBoard.getValue(r, c) == value)
                return true; // assignation is redundant, we already had it assigned so we give it as correct
            /* FIXME: DEBUGGING PURPOSES abortion reason
            * System.out.println("ABORTING: either value is in row or column or cell is already assigned at coord " + r + ", " + c);
            * System.out.println("Value: " + value + " - rowHasValue: " + rowValuesUsed[rowID][value-1] + " - colHasValue: " + colValuesUsed[colID][value-1] + " - hasAssigned: " + !workingBoard.isEmpty(r, c));
            */
            return false;
        }
        cellValueRollBack.add(new Coordinates(r, c)); //if rollback we clear this coordinates and insert in notationsQueue
        notationsQueue.removeOrderedCell(r, c); // removes it from queue but notations are mantained
        workingBoard.setCellValue(r, c, value);
        rowValuesUsed[rowID][value-1] = true;
        colValuesUsed[colID][value-1] = true;
        boolean success = true;
        success = success && updateRowNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
        success = success && updateColNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
        return success;
    }

    private boolean updateRowNotations(int r, int c, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // FIXME: DEBUG System.out.println("updateRowNotations for coord " + r + "," + c);
        //updates the notations of the row and can cause assignations, returns whether the update was successful
        int rowID = rowLine[r][c];
        ArrayList<Integer> affectedColumns = new ArrayList<>();

        boolean[] rowOptions = new boolean[] { false, false, false, false, false, false, false, false, false };

        if (rowSums[rowID] != 0) { // row sum is assigned
            // get possible cases for the row
            ArrayList<ArrayList<Integer>> possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(rowSize[rowID], rowSums[rowID], rowValuesUsed[rowID]);
            for (ArrayList<Integer> opt : possibleCases)
                for (Integer v : opt)
                    rowOptions[v - 1] = true;

        } else { // row sum is NOT assigned
            // get possible cases for the row
            ArrayList<Pair<Integer, ArrayList<Integer>>> possibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValuesUsed[rowID]);
            int onlySum = -1;
            for (Pair<Integer, ArrayList<Integer>> p : possibleCases) {
                if (onlySum == -1) onlySum = p.first;
                else if (onlySum != p.first) onlySum = -2;
                for (int opt : p.second) rowOptions[opt -1] = true;
            }
            if (onlySum > 0) { // only one sum is possible for this space and values, we assign it
                rowSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
            }
        }

        // subtract the already used values
        boolean[] commonRowNotations = new boolean[] { false, false, false, false, false, false, false, false, false };
        for (int i = 0; i < 9; i++)
            commonRowNotations[i] = rowOptions[i] && !rowValuesUsed[rowID][i];

        // check for each non-set white-cell if its notations have som notation that is not in commonRowNotations
        // if so, erase notations, mark column as affected, add cell notations to rollback
        int initialCol = c;
        boolean foundInitCol = false;
        for (int i = 1; !foundInitCol; i++) {
            if (workingBoard.isBlackCell(r, c-i)) { initialCol = c - i + 1; foundInitCol = true; }
            else if (c+i >= columns || workingBoard.isBlackCell(r, c+i)) { initialCol = c+i-rowSize[rowID]; foundInitCol = true; }
        }
        for(int it = initialCol; it < initialCol+rowSize[rowID]; it++) {
            // FIXME: DEBUG System.out.println("ITERATING ROW AT COORD: " + r + "," + c + " Initial col at: " + initialCol + " Row size: " + rowSize[rowID] + " and IT: " + it);
            if (workingBoard.isEmpty(r, it)) { //value not set
                boolean[] cellNotations = workingBoard.getCellNotations(r, it);
                boolean[] rollbackNotations = new boolean[9]; // IMPORTANT! rollback notations should be a new object
                // because cellNotations might get modified if we have to erase, rollback holds the original values
                ArrayList<Integer> toErase = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    rollbackNotations[i] = cellNotations[i];
                    if(cellNotations[i] && !commonRowNotations[i]) toErase.add(i+1);
                }
                if (toErase.size() > 0) { // we need to erase some notations
                    affectedColumns.add(it);
                    cellNotationsRollBack.add(new RollbackNotations(r, it, rollbackNotations));
                    notationsQueue.eraseNotationsFromCell(r, it, toErase);
                }
            }
        }

        boolean success = true;
        for (int affected : affectedColumns) {
            int notationSize = workingBoard.getCellNotationSize(r, affected);
            if (notationSize == 0) {
                // FIXME: DEBUGGING PURPOSES abortion reason
                // System.out.println("ABORTING: no values are possible in " + r + ", " + affected);
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                boolean[] cellNotations = workingBoard.getCellNotations(r, affected);
                for (int i = 0; value == -1 && i < 9; i++) if(cellNotations[i]) value = i+1;
                success = success && cellValueAssignation(r, affected, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else success = success && updateColNotations(r, affected, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack); //all must be successful
            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    private boolean updateColNotations(int r, int c, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // FIXME: DEBUG System.out.println("updateColNotations for coord " + r + "," + c);
        //updates the notations of the column and can cause assignations, returns whether the update was successful
        int colID = colLine[r][c];
        ArrayList<Integer> affectedRows = new ArrayList<>();

        boolean[] colOptions = new boolean[] { false, false, false, false, false, false, false, false, false };

        if (colSums[colID] != 0) { // col sum is assigned
            // get possible cases for the col
            ArrayList<ArrayList<Integer>> possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(colSize[colID], colSums[colID], colValuesUsed[colID]);
            for (ArrayList<Integer> opt : possibleCases)
                for (Integer v : opt)
                    colOptions[v - 1] = true;

        } else { // col sum is NOT assigned
            // get possible cases for the col
            ArrayList<Pair<Integer, ArrayList<Integer>>> possibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValuesUsed[colID]);
            int onlySum = -1;
            for (Pair<Integer, ArrayList<Integer>> p : possibleCases) {
                if (onlySum == -1) onlySum = p.first;
                else if (onlySum != p.first) onlySum = -2;
                for (int opt : p.second) colOptions[opt -1] = true;
            }
            if (onlySum > 0) { // only one sum is possible for this space and values, we assign it
                colSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
            }
        }

        // subtract the already used values
        boolean[] commonColNotations = new boolean[] { false, false, false, false, false, false, false, false, false };
        for (int i = 0; i < 9; i++)
            commonColNotations[i] = colOptions[i] && !colValuesUsed[colID][i];

        // check for each non-set white-cell if its notations have some notation that is not in commonColNotations
        // if so, erase notations, mark row as affected, add cell notations to rollback
        int initialRow = r;
        boolean foundInitRow = false;
        for (int i = 1; !foundInitRow; i++) {
            if (workingBoard.isBlackCell(r-i, c)) { initialRow = r - i + 1; foundInitRow = true; }
            else if (r+i >= rows || workingBoard.isBlackCell(r+i, c)) { initialRow = r+i-colSize[colID]; foundInitRow = true; }
        }
        for(int it = initialRow; it < initialRow+colSize[colID]; it++) {
            // FIXME: DEBUG System.out.println("ITERATING COLUMN AT COORD: " + r + "," + c + " Initial row at: " + initialRow + " Col size: " + colSize[colID] + " and IT: " + it);
            if (workingBoard.isEmpty(it, c)) { //value not set
                boolean[] cellNotations = workingBoard.getCellNotations(it, c);
                boolean[] rollbackNotations = new boolean[9]; // IMPORTANT! rollback notations should be a new object
                // because cellNotations might get modified if we have to erase, rollback holds the original values
                ArrayList<Integer> toErase = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    rollbackNotations[i] = cellNotations[i];
                    if(cellNotations[i] && !commonColNotations[i]) toErase.add(i+1);
                }
                if (toErase.size() > 0) { // we need to erase some notations
                    affectedRows.add(it);
                    cellNotationsRollBack.add(new RollbackNotations(it, c, rollbackNotations));
                    notationsQueue.eraseNotationsFromCell(it, c, toErase);
                }
            }
        }

        boolean success = true;
        for (int affected : affectedRows) {
            int notationSize = workingBoard.getCellNotationSize(affected, c);
            if (notationSize == 0) {
                // FIXME: DEBUGGING PURPOSES abortion reason
                // System.out.println("ABORTING: no values are possible in " + affected + ", " + c);
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                boolean[] cellNotations = workingBoard.getCellNotations(affected, c);
                for (int i = 0; value == -1 && i < 9; i++) if(cellNotations[i]) value = i+1;
                success = success && cellValueAssignation(affected, c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else success = success && updateColNotations(affected, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack); //all must be successful
            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    private void rollBack(ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // Row sums
        for (int row : rowSumRollBack) {
            rowSums[row] = 0;
        }
        // Col sums
        for (int col : colSumRollBack) {
            colSums[col] = 0;
        }
        // Cell value
        for (Coordinates c : cellValueRollBack) {
            notationsQueue.insertOrderedCell(c.r, c.c); // adds it to queue with previous notations
            int value = workingBoard.getValue(c.r, c.c);
            workingBoard.clearCellValue(c.r, c.c);
            rowValuesUsed[rowLine[c.r][c.c]][value-1] = false;
            colValuesUsed[colLine[c.r][c.c]][value-1] = false;
        }
        // Cell notations
        // notice that it is important to first insert the cells if needed, because if we don't the datastructure
        // will not consider it as valid and it won't find it to add the notations
        for (RollbackNotations n : cellNotationsRollBack) {
            Coordinates c = n.coord;
            ArrayList<Integer> toAdd = new ArrayList<>();
            for (int i = 0; i < 9; i++) if (n.notations[i]) toAdd.add(i+1);
            notationsQueue.addNotationsToCell(c.r, c.c, toAdd);
        }
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

        /* FIXME: DEBUGGING PURPOSES
        * printData();
        * printNotations();
         */

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
        int uniqueAssigned = 0;
        for (int start = 0; start < numOfStartingPoints; start++) {

            int coordRow;
            int coordCol;

            if (random.nextBoolean()) { // row based assignation
                int rowID = random.nextInt(rowLineSize);
                int offset = random.nextInt(rowSize[rowID]);
                coordRow = firstRowCoord[rowID].r;
                coordCol = firstRowCoord[rowID].c + offset;
            } else { // column based assignation
                int colID = random.nextInt(colLineSize);
                int offset = random.nextInt(colSize[colID]);
                coordRow = firstColCoord[colID].r + offset;
                coordCol = firstColCoord[colID].c;
            }

            // FIXME: DEBUGGING PURPOSES
            //System.out.println("-------------"+start+"----------row: "+coordRow+"------col: "+coordCol+"-------------");

            if (workingBoard.isEmpty(coordRow, coordCol)) { // in case a previous assignation has assigned this cell's value
                ArrayList<int[]> uniqueCrossValues = KakuroConstants.INSTANCE.getUniqueCrossValues(rowSize[rowLine[coordRow][coordCol]], colSize[colLine[coordRow][coordCol]], difficulty); // returns [] of {rowSum, colSum, valueInCommon}
                boolean success = false;
                for (int i = 0; !success && i < uniqueCrossValues.size(); i++) {
                    int[] uniqueValue = uniqueCrossValues.get(i);
                    // Assign uniqueValue[0] to the row sum, uniqueValue[1] to de column sum
                    //  and uniqueValue[2] to de WhiteCell value in [coordRow][coordCol]
                    //  these assignments will take care to modify the notations and call other
                    //  assignments recursively as well as change the pointers to orderedCells accordingly,
                    //  if an assignment is successful we shouldn't call the next ones;
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    success = true;
                    success = success && rowSumAssignation(coordRow, coordCol, uniqueValue[0], rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    success = success && colSumAssignation(coordRow, coordCol, uniqueValue[1], rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    // the row and sum assignation should assign the unique value correctly
                    if (success) {
                        /* FIXME: DEBUGGING PURPOSES
                        * System.out.println("Assignation successful: rowSums: ");
                        * System.out.println("Coord: " + coordRow + "," + coordCol + " Row sum: " + uniqueValue[0] + ", Col sum: " + uniqueValue[1] + ", Unique value: " + uniqueValue[2]);
                        * printNotations();
                         */
                        uniqueAssigned ++;
                    }
                    else rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }
            }
        }
        /* FIXME: DEBUGGING PURPOSES
        * System.out.println("Assigned: " + uniqueAssigned + " unique values, but tried: " + numOfStartingPoints);
        * printNotations();
        * return;
        */

        // At this point we've had a number of successful starting point assignments, now we assign the WhiteCell
        // that has the least notations (possible values) in a way that makes it non ambiguous

        while (!notationsQueue.isEmpty()) {
            // then we have elements with no known value so we must do an assignation
            // TODO: We should be able to get the cell's coordinates from the Cell object. Maybe every cell should save its coord.
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

    // FIXME: DEBUGGING PURPOSES
    public void printData(){
        System.out.println("Board: ");
        if (workingBoard != null) System.out.println("Board is not null");
        System.out.println();
        System.out.println(workingBoard.toString());

        System.out.println("Row IDs: ");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int rowID = rowLine[i][j];
                if (rowID < 0 || rowID/10>0) System.out.print("["+rowID+"] ");
                else System.out.print("[ "+rowID+"] ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Row sizes: ");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isWhiteCell(i,j)) {
                    int rowID = rowLine[i][j];
                    int rows = rowSize[rowID];
                    System.out.print("["+rows+"] ");
                } else {
                    System.out.print("[*] ");
                }
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Col IDs: ");
        for (int i = 0; i <rows; i++) {
            for (int j = 0; j < columns; j++) {
                int colID = colLine[i][j];
                if (colID < 0 || colID/10>0) System.out.print("["+colID+"] ");
                else System.out.print("[ "+colID+"] ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("Col sizes: ");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isWhiteCell(i,j)) {
                    int colID = colLine[i][j];
                    int cols = colSize[colID];
                    System.out.print("["+cols+"] ");
                } else {
                    System.out.print("[*] ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    // FIXME: DEBUGGING PURPOSES
    private void printNotations() {
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isWhiteCell(i, j)) {
                    boolean[] b = workingBoard.getCellNotations(i, j);
                    System.out.print("[");
                    for (int k = 0; k < 9; k++) {
                        if (b[k]) System.out.print((k+1)+"");
                        else System.out.print("-");
                    }
                    System.out.print("] ");
                }
                else {
                    int hs = workingBoard.getHorizontalSum(i, j);
                    if (rowLine[i][j] != -1) hs = rowSums[rowLine[i][j]];
                    int vs = workingBoard.getVerticalSum(i, j);
                    if (colLine[i][j] != -1) vs = colSums[colLine[i][j]];
                    if (hs / 10 > 0) System.out.print("[*" + hs + "*-*");
                    else System.out.print("[**" + hs + "*-*");
                    if (vs / 10 > 0) System.out.print(vs + "*] ");
                    else System.out.print("*" + vs + "*] ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
    //

    private class Coordinates {
        public int r, c;
        public Coordinates(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    private class RollbackNotations {
        public Coordinates coord;
        public boolean[] notations;
        public RollbackNotations(int r, int c, boolean[] n) {
            coord = new Coordinates(r, c);
            notations = n;
        }
    }
}
