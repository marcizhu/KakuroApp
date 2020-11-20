package src.controllers;

import src.domain.*;
import src.utils.Pair;

import java.util.*;

public class Generator {

    private Board generatedBoard;
    private Board workingBoard;
    private SwappingCellQueue notationsQueue;

    private int rows;
    private int columns;
    private Difficulty difficulty;

    private Random random;

    private int[] rowSums;                  // value of sums assigned to rowLines, 0 means not assigned
    private int[] rowSize;                  // sizes of each rowLine, always between 0 and 9
    private boolean[][] rowValuesUsed;      // cell values used (assigned) in each rowLine, always boolean[9]
    private Coordinates[] firstRowCoord;    // coordinates to the first white cell in each rowLine
    private final int[][] rowLine;          // Pointers to position at arrays of row related data
    private int rowLineSize;                // Number of different rowLines

    private int[] colSums;                  // value of sums assigned to colLines, 0 means not assigned
    private int[] colSize;                  // sizes of each colLine, always between 0 and 9
    private boolean[][] colValuesUsed;      // cell values used (assigned) in each colLine, always boolean[9]
    private Coordinates[] firstColCoord;    // coordinates to the first white cell in each colLine
    private final int[][] colLine;          // Pointers to position at array of colValuesUsed and colSums
    private int colLineSize;                // Number of different colLines

    public Generator(int rows, int columns, Difficulty difficulty) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        rowLine = new int[rows][columns];
        colLine = new int[rows][columns];
        this.random = new Random();
        long seed = random.nextLong();
        this.random = new Random(seed);
    }

    public Generator(int rows, int columns, Difficulty difficulty, long seed) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        rowLine = new int[rows][columns];
        colLine = new int[rows][columns];
        this.random = new Random(seed);
    }

    public Board getGeneratedBoard() {
        return generatedBoard;
    }

    private boolean isValidPosition(Board b, int row, int col) {
        /*
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

    private Board prepareWorkingBoard() {
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
    private boolean rowSumAssignation(int r, int c, int value, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        // Should update the row sum for a given coordinates to the value and add row to rollback
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        int rowID = rowLine[r][c];
        if (rowSums[rowID] != 0) {
            return false; //already has a sum value assigned
        }
        rowSums[rowID] = value;
        modifiedRows[rowID] = true;
        rowSumRollBack.add(rowID);
        return updateRowNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
    }

    private boolean colSumAssignation(int r, int c, int value, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        // Should update the col sum for a given coordinates to the value, and add column to rollback
        //  when in doubt, a sum assignation should be called before a cellValue
        //  assignation because it is more restrictive
        //  Could call other assignations recursively
        int colID = colLine[r][c];
        if (colSums[colID] != 0) {
            return false; //already has a sum value assigned
        }
        colSums[colID] = value;
        modifiedCols[colID] = true;
        colSumRollBack.add(colID);
        return updateColNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
    }

    private boolean cellValueAssignation(int r, int c, int value, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        // Should update the assignation for that cell, set the value, update the orderedCells data structure
        //  and its pointers with removeOrderedCell, update valuesUsed for row and col, and add it to rollback.
        //  Could call other assignations recursively
        int rowID = rowLine[r][c];
        int colID = colLine[r][c];
        if (rowValuesUsed[rowID][value-1] ||  colValuesUsed[colID][value-1] || !workingBoard.isEmpty(r, c)){
            if (rowValuesUsed[rowID][value-1] && colValuesUsed[colID][value-1] && workingBoard.getValue(r, c) == value) {
                //System.out.println("Redundant cellValue assignation at: " + r + ", " + c + " with value: " + value);
                return true; // assignation is redundant, we already had it assigned so we give it as correct
            }
            return false;
        }

        //check that adding this value doesn't make the whole sum greater than it has to be for assigned sums
        if (rowSums[rowID] != 0 || colSums[colID] != 0) {
            int rowS = 0, colS = 0;
            for (int i = 0; i < 9; i++) {
                if (rowValuesUsed[rowID][i]) rowS += (i+1);
                if (colValuesUsed[colID][i]) colS += (i+1);
            }
            if ((rowSums[rowID] != 0 && rowS+value > rowSums[rowID]) || (colSums[colID] != 0 && colS+value > colSums[colID]))
                return false; //new sum would be greater than it should
        }

        cellValueRollBack.add(new Coordinates(r, c)); //if rollback we clear this coordinates and insert in notationsQueue
        notationsQueue.removeOrderedCell(r, c); // removes it from queue but notations are mantained
        workingBoard.setCellValue(r, c, value);
        rowValuesUsed[rowID][value-1] = true;
        colValuesUsed[colID][value-1] = true;
        modifiedRows[rowID] = true;
        modifiedCols[colID] = true;
        boolean success = true;
        success = success && updateRowNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        success = success && updateColNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
        return success;
    }

    private boolean updateRowNotations(int r, int c, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        //updates the notations of the row and can cause assignations, returns whether the update was successful
        int rowID = rowLine[r][c];
        ArrayList<Integer> affectedColumns = new ArrayList<>();

        ArrayList<ArrayList<Integer>> possibleCases;

        if (rowSums[rowID] != 0) { // row sum is assigned
            // get possible cases for the row
            possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(rowSize[rowID], rowSums[rowID], rowValuesUsed[rowID]);

        } else { // row sum is NOT assigned
            // get possible cases for the row
            ArrayList<Pair<Integer, ArrayList<Integer>>> multiplePossibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValuesUsed[rowID]);
            possibleCases = new ArrayList<>();
            int onlySum = -1;
            for (Pair<Integer, ArrayList<Integer>> p : multiplePossibleCases) {
                if (onlySum == -1) onlySum = p.first;
                else if (onlySum != p.first) onlySum = -2;
                possibleCases.add(p.second);
            }
            if (onlySum > 0) { // only one sum is possible for this space and values, we assign it
                return rowSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
            }
        }

        // validate combinations or erase if not valid
        for (int i = possibleCases.size()-1; i >= 0; i--) {
            ArrayList<Integer> p = possibleCases.get(i);
            ArrayList<WhiteCell> containingCells = new ArrayList<>();
            for (int it = firstRowCoord[rowID].c; it < firstRowCoord[rowID].c+rowSize[rowID]; it++) {
                for (int digit : p) {
                    if ((!workingBoard.isEmpty(r, it) && workingBoard.getValue(r, it) == digit) || workingBoard.cellHasNotation(r, it, digit)) {
                        containingCells.add((WhiteCell)workingBoard.getCell(r, it));
                        break;
                    }
                }
            }
            if (!isCombinationPossible(p, containingCells)) possibleCases.remove(i);
        }

        boolean[] rowOptions = new boolean[] { false, false, false, false, false, false, false, false, false };
        for(ArrayList<Integer> p : possibleCases)
            for (int d : p) rowOptions[d-1] = true;

        // subtract the already used values
        boolean[] commonRowNotations = new boolean[] { false, false, false, false, false, false, false, false, false };
        boolean superPermissive = true;
        for (int i = 0; i < 9; i++) {
            commonRowNotations[i] = rowOptions[i] && !rowValuesUsed[rowID][i];
            if (!commonRowNotations[i]) superPermissive = false;
        }

        // check for each non-set white-cell if its notations have some notation that is not in commonRowNotations
        // if so, erase notations, mark column as affected, add cell notations to rollback

        for(int it = firstRowCoord[rowID].c; !superPermissive && it < firstRowCoord[rowID].c+rowSize[rowID]; it++) {
            if (workingBoard.isEmpty(r, it)) { //value not set
                boolean[] cellNotations = workingBoard.getCellNotations(r, it);
                boolean[] rollbackNotations = new boolean[9]; // IMPORTANT! rollback notations should be a new object
                // because cellNotations might get modified if we have to erase, rollback holds the original values
                ArrayList<Integer> toErase = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    rollbackNotations[i] = cellNotations[i];
                    if(cellNotations[i] && !commonRowNotations[i]) toErase.add(i + 1); // if it isn't part of the validated possible notations for the row
                }
                if (toErase.size() > 0) { // we need to erase some notations
                    modifiedRows[rowID] = true;
                    modifiedCols[colLine[r][it]] = true;
                    affectedColumns.add(it);
                    if (notationsQueue.isHiding(r, it)) hidingCellNotationsRollBack.add(new RollbackNotations(r, it, rollbackNotations));
                    else cellNotationsRollBack.add(new RollbackNotations(r, it, rollbackNotations));
                    notationsQueue.eraseNotationsFromCell(r, it, toErase);
                }
            }
        }

        boolean success = true;
        if (affectedColumns.size() == 0) modifiedRows[rowID] = false; //the cells on this row were not modified.
        for (int affected : affectedColumns) {
            int notationSize = workingBoard.getCellNotationSize(r, affected);
            if (notationSize == 0) {
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                boolean[] cellNotations = workingBoard.getCellNotations(r, affected);
                for (int i = 0; value == -1 && i < 9; i++) if(cellNotations[i]) value = i+1;
                success = success && cellValueAssignation(r, affected, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else {
                success = success && updateColNotations(r, affected, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful
                if (modifiedRows[rowID]) success = success && updateRowNotations(r, affected, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful
            }

            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    private boolean updateColNotations(int r, int c, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack, boolean[] modifiedRows, boolean[] modifiedCols) {
        //updates the notations of the column and can cause assignations, returns whether the update was successful
        int colID = colLine[r][c];
        ArrayList<Integer> affectedRows = new ArrayList<>();

        ArrayList<ArrayList<Integer>> possibleCases;

        if (colSums[colID] != 0) { // col sum is assigned
            // get possible cases for the col
            possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(colSize[colID], colSums[colID], colValuesUsed[colID]);

        } else { // col sum is NOT assigned
            // get possible cases for the col
            ArrayList<Pair<Integer, ArrayList<Integer>>> multiplePossibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValuesUsed[colID]);
            possibleCases = new ArrayList<>();
            int onlySum = -1;
            for (Pair<Integer, ArrayList<Integer>> p : multiplePossibleCases) {
                if (onlySum == -1) onlySum = p.first;
                else if (onlySum != p.first) onlySum = -2;
                possibleCases.add(p.second);
            }
            if (onlySum > 0) { // only one sum is possible for this space and values, we assign it
                return colSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
            }
        }

        // validate combinations or erase if not valid
        for (int i = possibleCases.size()-1; i >= 0; i--) {
            ArrayList<Integer> p = possibleCases.get(i);
            ArrayList<WhiteCell> containingCells = new ArrayList<>();
            for (int it = firstColCoord[colID].r; it < firstColCoord[colID].r+colSize[colID]; it++) {
                for (int digit : p) {
                    if ((!workingBoard.isEmpty(it, c) && workingBoard.getValue(it, c) == digit) || workingBoard.cellHasNotation(it, c, digit)) {
                        containingCells.add((WhiteCell)workingBoard.getCell(it, c));
                        break;
                    }
                }
            }
            if (!isCombinationPossible(p, containingCells)) possibleCases.remove(i);
        }

        boolean[] colOptions = new boolean[] { false, false, false, false, false, false, false, false, false };
        for(ArrayList<Integer> p : possibleCases)
            for (int d : p) colOptions[d-1] = true;

        // subtract the already used values
        boolean[] commonColNotations = new boolean[] { false, false, false, false, false, false, false, false, false };
        boolean superPermissive = true;
        for (int i = 0; i < 9; i++) {
            commonColNotations[i] = colOptions[i] && !colValuesUsed[colID][i];
            if (!commonColNotations[i]) superPermissive = false;
        }

        for(int it = firstColCoord[colID].r; !superPermissive && it < firstColCoord[colID].r+colSize[colID]; it++) {
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
                    modifiedRows[rowLine[it][c]] = true;
                    modifiedCols[colID] = true;
                    affectedRows.add(it);
                    if (notationsQueue.isHiding(it, c)) hidingCellNotationsRollBack.add(new RollbackNotations(it, c, rollbackNotations));
                    else cellNotationsRollBack.add(new RollbackNotations(it, c, rollbackNotations));
                    notationsQueue.eraseNotationsFromCell(it, c, toErase);
                }
            }
        }

        boolean success = true;
        if (affectedRows.size() == 0) modifiedCols[colID] = false; //the cells on this column were not modified.
        for (int affected : affectedRows) {
            int notationSize = workingBoard.getCellNotationSize(affected, c);
            if (notationSize == 0) {
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                boolean[] cellNotations = workingBoard.getCellNotations(affected, c);
                for (int i = 0; value == -1 && i < 9; i++) if(cellNotations[i]) value = i+1;
                success = success && cellValueAssignation(affected, c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else {
                success = success && updateRowNotations(affected, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful
                if (modifiedCols[colID]) success = success && updateColNotations(affected, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols); //all must be successful //TODO: analyze if this recursive call is actually needed
            }
            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    private boolean isCombinationPossible(ArrayList<Integer> comb, ArrayList<WhiteCell> cells) {
        if (cells.size() < comb.size() || comb.size() == 0) return false;
        if (comb.size() == 1) {
            int digit = comb.get(0);
            for (WhiteCell c : cells) {
                if ((!c.isEmpty() && c.getValue() == digit) || (c.isEmpty() && c.isNotationChecked(digit))) return true;
            }
            return false;
        }

        WhiteCell c = cells.get(0);
        cells.remove(0);
        boolean success = false;
        if (c.isEmpty()) {
            for (int j = comb.size()-1; !success && j >= 0; j--) {
                int digit = comb.get(j);
                if (c.isNotationChecked(digit)) {
                    comb.remove(j);
                    success = isCombinationPossible(comb, cells);
                    comb.add(j, digit);
                }
            }
        } else {
            for (int j = 0; j < comb.size(); j++) {
                if (comb.get(j) == c.getValue()) {
                    comb.remove(j);
                    success = isCombinationPossible(comb, cells);
                    comb.add(j, c.getValue());
                    cells.add(0, c);
                    return success;
                }
            }
        }
        cells.add(0, c);

        return success;
    }

    private void rollBack(ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack, ArrayList<RollbackNotations> hidingCellNotationsRollBack) {
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
        for (RollbackNotations n : hidingCellNotationsRollBack) {
            Coordinates c = n.coord;
            ArrayList<Integer> toAdd = new ArrayList<>();
            for (int i = 0; i < 9; i++) if (n.notations[i]) toAdd.add(i+1);
            notationsQueue.addNotationsToCell(c.r, c.c, toAdd);
            notationsQueue.hideElement(c.r, c.c);
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

        // Select some random white cells within possible starting points to begin the assignations, depending on difficulty
        ArrayList<Coordinates> possibleStartingPoints = new ArrayList<>();
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < columns; c++) {
                if (workingBoard.isWhiteCell(r, c)){
                    int rSize = rowSize[rowLine[r][c]];
                    int cSize = colSize[colLine[r][c]];
                    int min = rSize < cSize ? rSize : cSize;
                    int max = rSize > cSize ? rSize : cSize;
                    if (min == 1) possibleStartingPoints.add(new Coordinates(r, c));
                    else if (max != 9 && rSize+cSize <= 10) possibleStartingPoints.add(new Coordinates(r, c));
                }
            }
        }

        // shuffle the options randomly
        Collections.shuffle(possibleStartingPoints, random);

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
        while (uniqueAssigned < numOfStartingPoints && possibleStartingPoints.size() > 0) {

            Coordinates coord = possibleStartingPoints.get(0);
            int coordRow = coord.r;
            int coordCol = coord.c;
            possibleStartingPoints.remove(0);

            if (workingBoard.isEmpty(coordRow, coordCol)) { // in case a previous assignation has assigned this cell's value
                if (generateStartingPoint(coordRow, coordCol)) uniqueAssigned++;
            }
        }

        // At this point we've had a number of successful starting point assignments, now we assign the WhiteCell
        // that has the least notations (possible values) in a way that makes it non ambiguous
        TreeSet<Coordinates> possibleAmbiguities = new TreeSet<>();

        while (!notationsQueue.isEmpty()) {
            // then we have elements with no known value so we must do an assignation
            WhiteCell candidate = notationsQueue.getFirstElement(); //should never return a cell with value
            Pair<Integer, Integer> coord = candidate.getCoordinates();
            boolean isRowSumAssigned = rowSums[rowLine[coord.first][coord.second]] != 0;
            boolean isColSumAssigned = colSums[colLine[coord.first][coord.second]] != 0;
            // if one or both of the sums are not assigned, we should choose the value in
            // its notations that given the current values in the row and column there is a unique value for a
            // certain sum assignation
            if (!isRowSumAssigned || !isColSumAssigned) {
                boolean success = valueBiasedSumAssignation(coord.first, coord.second, workingBoard.getCellNotations(coord.first, coord.second));
                if (success) {
                    if (workingBoard.isEmpty(coord.first, coord.second)) { // if function above is working perfectly this shouldn't happen
                        if (notationsQueue.getFirstElement().equals(candidate))
                            notationsQueue.hideFirstElement();
                    } else {
                        notationsQueue.removeOrderedCell(coord.first, coord.second);
                    }
                    continue;
                }
            }

            // if both sums are already assigned, then that means there can still go more than one option in this
            // cell, first we see if one of its notations is unique among the notations of the row or of the column
            // if none are we try to find a cell in same row or column that has notated one of the notations of this cell
            // and has a row/column not assigned so that we can try to make it take the value of the specific notation
            // this way we reduce the ambiguity of the current cell possibilities.
            // if in the pursuite of finding a "responsible" cell for a notation of this one, we end up in this one,
            // (i.e. all cells with ambiguity-creating notations have their row/col sums assigned and have no unique notation)
            // there is ambiguity for that value and we should assign one of its values, the board will have more than one solution
            else {
                /*
                boolean success;
                boolean[] interestNotations = candidate.getNotations();

                // call function to find responsible for undoing ambiguity
                // TODO: ambiguitySolver needs to be checked, it's not ready for the first deliver

                boolean[][] visited = new boolean[rows][columns];
                success = ambiguitySolver(coord.first, coord.second, visited, coord.first, coord.second, interestNotations, true);
                if (!success) success = ambiguitySolver(coord.first, coord.second, visited, coord.first, coord.second, interestNotations, false);
                if (success) {
                    // let's check if our cell now has value
                    if (!workingBoard.isEmpty(coord.first, coord.second)) {
                        notationsQueue.removeOrderedCell(coord.first, coord.second);
                        continue; // ambiguity solved
                    }
                }*/
            }
            // seems like a possible ambiguity

            if (workingBoard.isEmpty(coord.first, coord.second)) {
                WhiteCell currentFirstElem = notationsQueue.getFirstElement();
                if (currentFirstElem.equals(candidate)) {
                    notationsQueue.hideFirstElement();
                } else if (candidate.getNotationSize() == currentFirstElem.getNotationSize()) {
                    notationsQueue.hideElement(coord.first, coord.second);
                }
                possibleAmbiguities.add(new Coordinates(coord.first, coord.second));
            } else {
                notationsQueue.removeOrderedCell(coord.first, coord.second);
            }
        }

        // when we get out of the while loop we should have a filled board generated,
        // maybe we want to send it to the solver to check if it's unique or not or check for permutations, etc.

        /*for (Coordinates c : possibleAmbiguities) {
            if (workingBoard.isEmpty(c.r, c.c)) {
                toSolve.add(c);
                System.out.println("Ambiguous white cell at: " + c.r + "," + c.c);
                foundAmbiguous = true;
            }
        }*/

        // FIXME:: THIS IS A PROVISIONAL FIX THAT CREATES TOTALLY AMBIGUOUS KAKUROS, JUST TO HAVE SOMETHING FOR THE FIRST DELIVER

        // IN PROVISIONAL VERSION WE ASSIGN ANY VALUE NOT IN THE ROW OR COLUMN AND WILL RE-ADAPT THE SUMS LATER
        // BECAUSE IF IT DIDN'T GET ASSIGNED IT WONT BE ABLE TO ASSIGN IT NOW EITHER. IF THE UPDATE OF POSSIBILITIES
        // MECHANISM IS WORKING WELL THIS SHOULD NEVER HAPPEN, THERE MUST ALWAYS BE AT LEAST ONE POSSIBILITY AVAILABLE
        ArrayList<Coordinates> toSolve = new ArrayList<>();
        boolean foundAmbiguous = false;
        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < columns; j++) {
                if (workingBoard.isWhiteCell(i,j) && workingBoard.isEmpty(i, j)) {
                    foundAmbiguous = true;
                    toSolve.add(new Coordinates(i, j));
                }
            }
        }
        if (toSolve.size() > 0) provisionalFillInBacktracking(toSolve);
        // IN FINAL VERSION WE NEED TO DECIDE IF WE FILL IT IN WITH AN AMBIGUOUS NUMBER AND LET IT AMBIGUOUS
        // OR AVOID AMBIGUITY BUT GENERATED BOARD WILL MAYBE HAVE WHITE CELLS WITH INITIALLY SET VALUES.
        // for (Coordinates c : possibleAmbiguities) {...}

        if (foundAmbiguous) { // we probably added values that do not correspond with the row and col sums
            computeRowSums();
            computeColSums();
        } else { // all values in row sums and col sums have preserved their integrity correctly
            defineBlackCellSums();
        }

        generatedBoard = new Board(columns, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isBlackCell(i, j)) {
                    generatedBoard.setCell(new BlackCell((BlackCell)workingBoard.getCell(i, j)), i, j);
                } else {
                    generatedBoard.setCell(new WhiteCell(), i, j);
                }
            }
        }
    }

    private boolean generateStartingPoint (int coordRow, int coordCol) {
        ArrayList<int[]> uniqueCrossValues = KakuroConstants.INSTANCE.getUniqueCrossValues(rowSize[rowLine[coordRow][coordCol]], colSize[colLine[coordRow][coordCol]], difficulty); // returns [] of {rowSum, colSum, valueInCommon}
        for (int i = 0; i < uniqueCrossValues.size(); i++) {
            int[] uniqueValue = uniqueCrossValues.get(i);
            // Assign uniqueValue[0] to the row sum, uniqueValue[1] to de column sum, cell value should update automatically
            //  these assignments will take care to modify the notations and call other
            //  assignments recursively as well as change the pointers in notationsQueue as needed.
            //  if an assignment is successful we shouldn't call the next ones;
            ArrayList<Integer> rowSumRollBack = new ArrayList<>();
            ArrayList<Integer> colSumRollBack = new ArrayList<>();
            ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
            ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
            ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
            boolean[] modifiedRows = new boolean[rowLineSize]; //default to false
            boolean[] modifiedCols = new boolean[colLineSize]; //default to false
            boolean success = true;
            success = success && rowSumAssignation(coordRow, coordCol, uniqueValue[0], rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
            success = success && colSumAssignation(coordRow, coordCol, uniqueValue[1], rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
            if (success) return true;
            else rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
        }
        return false;
    }

    private void defineBlackCellSums() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isBlackCell(i, j)) {
                    int rowSum = 0;
                    if (rowLine[i][j] != -1) rowSum = rowSums[rowLine[i][j]];
                    int colSum = 0;
                    if (colLine[i][j] != -1) colSum = colSums[colLine[i][j]];
                    workingBoard.setCell(new BlackCell(colSum, rowSum), i, j);
                }
            }
        }
    }

    private void computeRowSums() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns - 1; j++) {
                if (workingBoard.isBlackCell(i, j) && workingBoard.isWhiteCell(i, j+1)) {
                    int k = j;
                    int rowSum = 0;
                    for (j = j+1; j<columns && workingBoard.isWhiteCell(i, j); j++) {
                        rowSum += workingBoard.getValue(i, j);
                    }
                    j--;
                    workingBoard.setCell(new BlackCell(0, rowSum), i, k);
                }
            }
        }
    }

    private void computeColSums() {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows - 1; j++) {
                if (workingBoard.isBlackCell(j, i) && workingBoard.isWhiteCell(j+1, i)) {
                    int colSum = 0;
                    int k = j;
                    for (j = j+1; j<rows && workingBoard.isWhiteCell(j, i); j++) {
                        colSum += workingBoard.getValue(j, i);
                    }
                    j--;
                    int rowSum = workingBoard.getHorizontalSum(k, i);
                    workingBoard.setCell(new BlackCell(colSum, rowSum), k, i);
                }
            }
        }
    }

    private void provisionalFillInBacktracking(ArrayList<Coordinates> toSolve) {
        if (!fillWhiteCells(toSolve, 0)) System.out.println("No correct assignment of values found");
    }

    private boolean fillWhiteCells (ArrayList<Coordinates> coord, int pos) {
        if (pos >= coord.size()) return true;

        int r = coord.get(pos).r;
        int c = coord.get(pos).c;
        int rowID = rowLine[r][c];
        int colID = colLine[r][c];

        ArrayList<Integer> possibleValues = notUsedValues(rowID, colID);
        Collections.shuffle(possibleValues);

        for (int val: possibleValues) {
            int currentVal = workingBoard.getValue(r, c);
            boolean currentUsedR = rowValuesUsed[rowID][val-1];
            boolean currentUsedC = colValuesUsed[colID][val-1];

            workingBoard.setCellValue(r, c, val);
            rowValuesUsed[rowID][val-1] = true;
            colValuesUsed[colID][val-1] = true;

            if (fillWhiteCells(coord, pos+1)) return true;

            if (currentVal == 0) workingBoard.clearCellValue(r, c);
            else workingBoard.setCellValue(r, c, currentVal);
            rowValuesUsed[rowID][val-1] = currentUsedR;
            colValuesUsed[colID][val-1] = currentUsedC;
        }

        return false;
    }

    private ArrayList<Integer> notUsedValues(int rowID, int colID) {
        // Returns a list of the values that have not been used in the row and column
        ArrayList<Integer> values = new ArrayList<>();

        for(int i = 1; i<=9; i++) {
            if (!rowValuesUsed[rowID][i-1] && !colValuesUsed[colID][i-1]) values.add(i);
        }

        return values;
    }

    private boolean valueBiasedSumAssignation(int r, int c, boolean[] interestedValues) {
        boolean isRowAssigned = rowSums[rowLine[r][c]] != 0;
        boolean isColAssigned = colSums[colLine[r][c]] != 0;
        if (isRowAssigned && isColAssigned) return false; // this function only works if at least one is not assigned
        int rowID = rowLine[r][c], colID = colLine[r][c];
        boolean[] rowValues = new boolean[9];
        boolean[] colValues = new boolean[9];
        for (int i = 0; i < 9; i++) { //copies of used values to be able to modify them
            rowValues[i] = rowValuesUsed[rowID][i];
            colValues[i] = colValuesUsed[colID][i];
        }

        if (!isRowAssigned && isColAssigned) { //try to assign a value to row to force cell[r][c] into taking a value of its notations
            boolean[] uniqueNotations = uniqueNotationsIn(r, c, workingBoard.getCellNotations(r, c), true);
            TreeSet<Integer> rowSumCandidates = new TreeSet<>();
            TreeSet<Integer> rowSumCandidatesRepeated = new TreeSet<>();
            for (int i = 0; i < 9; i++) {
                if (interestedValues[i]) {
                    rowValues[i] = true;
                    ArrayList<Pair<Integer, ArrayList<Integer>>> possibilities = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValues);
                    for (Pair<Integer, ArrayList<Integer>> poss : possibilities) {
                        boolean foundCandidate = true;
                        if (!uniqueNotations[i]) { //if i is a unique notation, any combination that contains it is useful
                            for (int p : poss.second) {
                                // if candidate combination includes a value in notations other than the one we need it's not useful
                                if (interestedValues[p-1] && p-1 != i) foundCandidate = false;
                            }
                        }
                        if (foundCandidate) {
                            if (rowSumCandidates.contains(poss.first)) rowSumCandidatesRepeated.add(poss.first);
                            else rowSumCandidates.add(poss.first);
                        }
                    }
                    rowValues[i] = false;
                }
            }
            rowSumCandidates.removeAll(rowSumCandidatesRepeated);
            if (rowSumCandidates.size() == 0) {
                // didn't find any candidates.  if there are still notations in cell, it should be possible to find a combination
                // unless there are too many combinations available, in that case no possibility is unique enough to force the value we need
                return false;
            } else {
                // let's try to make a row assignation, let's check first if we need the valueOfInterest
                boolean success = false;
                for(int rowSumCand : rowSumCandidates) {
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
                    boolean[] modifiedRows = new boolean[rowLineSize]; //default to false
                    boolean[] modifiedCols = new boolean[colLineSize]; //default to false
                    success = rowSumAssignation(r, c, rowSumCand, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
                    else break;
                }
                if (success) return true;
                return false;
            }
        } else if (isRowAssigned && !isColAssigned) {
            boolean[] uniqueNotations = uniqueNotationsIn(r, c, workingBoard.getCellNotations(r, c), false);
            TreeSet<Integer> colSumCandidates = new TreeSet<>();
            TreeSet<Integer> colSumCandidatesRepeated = new TreeSet<>();
            for (int i = 0; i < 9; i++) {
                if (interestedValues[i]) {
                    colValues[i] = true;
                    ArrayList<Pair<Integer, ArrayList<Integer>>> possibilities = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValues);
                    for (Pair<Integer, ArrayList<Integer>> poss : possibilities) {
                        boolean foundCandidate = true;
                        if (!uniqueNotations[i]) { //if i is a unique notation, any combination that contains it is useful
                            for (int p : poss.second) {
                                // if candidate combination includes a value in notations other than the one we need it's not useful
                                if (interestedValues[p-1] && p-1 != i) foundCandidate = false;
                            }
                        }
                        if (foundCandidate) {
                            if (colSumCandidates.contains(poss.first)) colSumCandidatesRepeated.add(poss.first);
                            else colSumCandidates.add(poss.first);
                        }
                    }
                    colValues[i] = false;
                }
            }
            colSumCandidates.removeAll(colSumCandidatesRepeated);
            if (colSumCandidates.size() == 0) {
                // didn't find any candidates.  if there are still notations in cell, it should be possible to find a combination
                // unless there are too many combinations available, in that case no possibility is unique enough to force the value we need
                return false;
            } else {
                // let's try to make a col assignation
                boolean success = false;
                for(int colSumCand : colSumCandidates) {
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
                    boolean[] modifiedRows = new boolean[rowLineSize]; //default to false
                    boolean[] modifiedCols = new boolean[colLineSize]; //default to false
                    success = colSumAssignation(r, c, colSumCand, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
                    else break;
                }
                if (success) return true;
                return false;
            }
        } else {
            // if no values are assigned to the row and column then we choose a new starting point (if we can)
            boolean shouldCreateStartingPoint = true;
            for (int i = 0; shouldCreateStartingPoint && i < 9; i++)
                if (rowValuesUsed[rowID][i] || colValuesUsed[colID][i]) shouldCreateStartingPoint = false;

            if (shouldCreateStartingPoint) {
                shouldCreateStartingPoint = false;
                int rSize = rowSize[rowLine[r][c]];
                int cSize = colSize[colLine[r][c]];
                int min = rSize < cSize ? rSize : cSize;
                int max = rSize > cSize ? rSize : cSize;
                if (min == 1) shouldCreateStartingPoint = true;
                else if (max != 9 && rSize+cSize <= 10) shouldCreateStartingPoint = true;
            }

            if (shouldCreateStartingPoint) {
                return generateStartingPoint(r, c);
            } else {
                // some values are assigned, take them into consideration in the search

                // GET THE ROW POSSIBILITIES
                ArrayList<Pair<Integer, ArrayList<Integer>>> rowCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValuesUsed[rowID]);
                // validate combinations or erase if not valid
                // validate combinations or erase if not valid
                for (int i = rowCases.size()-1; i >= 0; i--) {
                    ArrayList<Integer> p = rowCases.get(i).second;
                    ArrayList<WhiteCell> containingCells = new ArrayList<>();
                    for (int it = firstRowCoord[rowID].c; it < firstRowCoord[rowID].c+rowSize[rowID]; it++) {
                        for (int digit : p) {
                            if ((!workingBoard.isEmpty(r, it) && workingBoard.getValue(r, it) == digit) || workingBoard.cellHasNotation(r, it, digit)) {
                                containingCells.add((WhiteCell)workingBoard.getCell(r, it));
                                break;
                            }
                        }
                    }
                    if (!isCombinationPossible(p, containingCells)) rowCases.remove(i);
                }

                // GET THE COLUMN POSSIBILITIES
                ArrayList<Pair<Integer, ArrayList<Integer>>> colCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValuesUsed[colID]);
                // validate combinations or erase if not valid
                for (int i = colCases.size()-1; i >= 0; i--) {
                    ArrayList<Integer> p = colCases.get(i).second;
                    ArrayList<WhiteCell> containingCells = new ArrayList<>();
                    for (int it = firstColCoord[colID].r; it < firstColCoord[colID].r+colSize[colID]; it++) {
                        for (int digit : p) {
                            if ((!workingBoard.isEmpty(it, c) && workingBoard.getValue(it, c) == digit) || workingBoard.cellHasNotation(it, c, digit)) {
                                containingCells.add((WhiteCell)workingBoard.getCell(it, c));
                                break;
                            }
                        }
                    }
                    if (!isCombinationPossible(p, containingCells)) colCases.remove(i);
                }

                ArrayList<Pair<Integer, Integer>> uniqueSumCombination = new ArrayList<>();

                for (int i = 0; i < rowCases.size(); ) {
                    boolean[] rowValuesSeen = { false, false, false, false, false, false, false, false, false };
                    int rowSumOpt = rowCases.get(i).first;
                    while(i < rowCases.size() && rowCases.get(i).first == rowSumOpt) {
                        ArrayList<Integer> rowOption = rowCases.get(i).second;
                        for (Integer value : rowOption) {
                            rowValuesSeen[value-1] = true;
                        }
                        i++;
                    }

                    for (int j = 0; j < colCases.size(); ) {
                        boolean[] colValuesSeen = { false, false, false, false, false, false, false, false, false };
                        int colSumOpt = colCases.get(j).first;
                        while(j < colCases.size() && colCases.get(j).first == colSumOpt) {
                            ArrayList<Integer> colOption = colCases.get(j).second;
                            for (Integer value : colOption) {
                                colValuesSeen[value - 1] = true;
                            }
                            j++;
                        }

                        int uniqueCrossValuePos = -1;
                        for (int k = 0; k < 9 && uniqueCrossValuePos != -2; k++) {
                            if (rowValuesSeen[k] && colValuesSeen[k]) {
                                if (uniqueCrossValuePos == -1) uniqueCrossValuePos = k; // there is one value in common
                                else uniqueCrossValuePos = -2; // there is more than one value in common
                            }
                        }

                        if (uniqueCrossValuePos >= 0) {
                            uniqueSumCombination.add(new Pair<> (rowSumOpt, colSumOpt));
                        }
                    }
                }

                Collections.shuffle(uniqueSumCombination, random);

                for (Pair<Integer, Integer> uniqueComb : uniqueSumCombination) {
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
                    boolean[] modifiedRows = new boolean[rowLineSize]; //default to false
                    boolean[] modifiedCols = new boolean[colLineSize]; //default to false
                    boolean success = true;
                    success = success && rowSumAssignation(r, c, uniqueComb.first, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                    success = success && colSumAssignation(r, c, uniqueComb.second, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                    if (success) return true;
                    else rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
                }
            }
            return false;
        }
    }

    private boolean[] uniqueNotationsIn(int r, int c, boolean[] interestNotations, boolean isRow) {
        boolean[] uniqueNotations = new boolean[] { true, true, true, true, true, true, true, true, true };
        int ID = isRow ? rowLine[r][c] : colLine[r][c];
        int firstPos = isRow ? firstRowCoord[ID].c : firstColCoord[ID].r;
        int size = isRow ? rowSize[ID] : colSize[ID];
        for (int it = firstPos; it < firstPos+size; it++) {
            if ((isRow && it == c)||(!isRow && it == r)) continue;
            boolean[] itNotations = isRow ? workingBoard.getCellNotations(r, it) : workingBoard.getCellNotations(it, c);
            for (int i = 0; i < 9; i++) uniqueNotations[i] = uniqueNotations[i] && interestNotations[i] && !itNotations[i];
        }
        return uniqueNotations;
    }

    private boolean ambiguitySolver(int r, int c, boolean[][] visited, final int init_r, final int init_c, final boolean[] interestNotations, boolean isRow) {
        if (visited[r][c]) return false; //already seen this and didn't solve anything
        visited[r][c] = true;
        boolean success;
        // check row/col
        ArrayList<Coordinates> checkCross = new ArrayList<>();
        int ID = isRow ? rowLine[r][c] : colLine[r][c];
        int firstPos = isRow ? firstRowCoord[ID].c : firstColCoord[ID].r;
        int size = isRow ? rowSize[ID] : colSize[ID];
        for (int it = firstPos; it < firstPos+size; it++) {
            if ((isRow && visited[r][it]) || (!isRow && visited[it][c])) continue;
            boolean[] itNotations = isRow ? workingBoard.getCellNotations(r, it) : workingBoard.getCellNotations(it, c);
            for (int i = 0; i < 9; i++) {
                if (interestNotations[i] && itNotations[i]) {
                    boolean isOfInterest = isRow ? colSums[colLine[r][it]] == 0 : rowSums[rowLine[it][c]] == 0;
                    if (isOfInterest) {
                        int rowA = isRow ? r : it;
                        int colA = isRow ? it : c;
                        boolean[] assignmentAttempt = new boolean[9];
                        assignmentAttempt[i] = true;
                        success = valueBiasedSumAssignation(rowA, colA, assignmentAttempt);
                        if (!workingBoard.isEmpty(init_r, init_c)) return true; //ambiguity was successfuly solved
                        else if (success) {
                            // there was an assignation that didn't solve the ambiguity but made changes
                            return ambiguitySolver(r, c, visited, init_r, init_c, interestNotations, !isRow);
                            // FIXME: I'm not too sure this is the correct thing to do, or just return false.
                        }
                    } else {
                        checkCross.add(isRow ? new Coordinates(r, it) : new Coordinates(it, c));
                    }
                }
            }
        }
        for(Coordinates coord : checkCross) {
            success = ambiguitySolver(coord.r, coord.c, visited, init_r, init_c, interestNotations, !isRow);
            if (success) return true;
        }
        return false;
    }

    // FIXME: DEBUGGING PURPOSES
    public void printData(){
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
                    if (workingBoard.isEmpty(i, j)) {
                        boolean[] b = workingBoard.getCellNotations(i, j);
                        System.out.print("[");
                        for (int k = 0; k < 9; k++) {
                            if (b[k]) System.out.print((k+1)+"");
                            else System.out.print("-");
                        }
                        System.out.print("] ");
                    } else {
                        System.out.print("[++++" + workingBoard.getValue(i,j) + "++++] ");
                    }
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

    private class Coordinates implements Comparable {
        public int r, c;
        public Coordinates(int r, int c) {
            this.r = r;
            this.c = c;
        }

        @Override
        public int compareTo(Object o) {
            if (this.r == ((Coordinates)o).r && this.c == ((Coordinates)o).c) return 0;
            if (this.r == ((Coordinates)o).r) {
                if (this.c < ((Coordinates)o).c) return -1;
                return 1;
            }
            if (this.r < ((Coordinates)o).r) return -1;
            return 1;
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
