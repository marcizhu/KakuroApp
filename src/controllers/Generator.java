package src.controllers;

import src.domain.*;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

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
        // FIXME: DEBUG
        //System.out.println("rowSumAssignation for coord " + r + "," + c + " , value: " + value);
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
        // FIXME: DEBUG
        //System.out.println("colSumAssignation for coord " + r + "," + c + " , value: " + value);
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
        // FIXME: DEBUG
        //System.out.println("cellValueAssignation for coord " + r + "," + c + " , value: " + value);
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

            /* FIXME: DEBUGGING PURPOSES abortion reason
            * System.out.println("ABORTING: either value is in row or column or cell is already assigned at coord " + r + ", " + c);
            * System.out.println("Value: " + value + " - rowHasValue: " + rowValuesUsed[rowID][value-1] + " - colHasValue: " + colValuesUsed[colID][value-1] + " - hasAssigned: " + !workingBoard.isEmpty(r, c));
            */
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
        boolean success = true;
        success = success && updateRowNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
        success = success && updateColNotations(r, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
        return success;
    }

    private boolean updateRowNotations(int r, int c, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // FIXME: DEBUG        System.out.println("updateRowNotations for coord " + r + "," + c);
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
                //System.out.println("Only one possible sum is available: " + onlySum + " at row of: " + r + ", " + c);
                rowSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
            }
        }

        // validate combinations or erase if not valid
        for (int i = 0; i < possibleCases.size(); i++) {
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
            // FIXME: DEBUG            System.out.println("ITERATING ROW AT COORD: " + r + "," + c + " Initial col at: " + initialCol + " Row size: " + rowSize[rowID] + " and IT: " + it);
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
                 //System.out.println("ABORTING: no values are possible in " + r + ", " + affected);
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                boolean[] cellNotations = workingBoard.getCellNotations(r, affected);
                for (int i = 0; value == -1 && i < 9; i++) if(cellNotations[i]) value = i+1;
                //System.out.println("Trying to assign only possible value " + value + " in " + r + ", " + affected);
                success = success && cellValueAssignation(r, affected, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                //System.out.println("Value assignation success was: " + success + " in " + r + ", " + affected);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else {
                success = success && updateColNotations(r, affected, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack); //all must be successful
                success = success && updateRowNotations(r, affected, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack); //all must be successful
            }

            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    private boolean updateColNotations(int r, int c, ArrayList<Integer> rowSumRollBack, ArrayList<Integer> colSumRollBack, ArrayList<Coordinates> cellValueRollBack, ArrayList<RollbackNotations> cellNotationsRollBack) {
        // FIXME: DEBUG     System.out.println("updateColNotations for coord " + r + "," + c);
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
                //System.out.println("Only one possible sum is available: " + onlySum + " at col of: " + r + ", " + c);
                colSumAssignation(r, c, onlySum, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
            }
        }

        // validate combinations or erase if not valid
        for (int i = 0; i < possibleCases.size(); i++) {
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
            // FIXME: DEBUG   System.out.println("ITERATING COLUMN AT COORD: " + r + "," + c + " Initial row at: " + initialRow + " Col size: " + colSize[colID] + " and IT: " + it);
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
                 //System.out.println("ABORTING: no values are possible in " + affected + ", " + c);
                return false; // no values are possible for this empty cell, whole branch must do rollback
            }
            if (notationSize == 1) { // only one value possible, we assign it
                int value = -1;
                boolean[] cellNotations = workingBoard.getCellNotations(affected, c);
                for (int i = 0; value == -1 && i < 9; i++) if(cellNotations[i]) value = i+1;

                //System.out.println("Trying to assign only possible value " + value + " in " + r + ", " + affected);
                success = success && cellValueAssignation(affected, c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                //System.out.println("Value assignation success was: " + success + " in " + r + ", " + affected);
                // a cellValueAssignation already calls to updateRow and updateColumn
            }
            else {
                success = success && updateRowNotations(affected, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack); //all must be successful
                success = success && updateColNotations(affected, c, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack); //all must be successful //TODO: analyze if this recursive call is actually needed
            }
            if (!success) return false; // responsible for the call will do rollbacks
        }
        return true;
    }

    private boolean isCombinationPossible(ArrayList<Integer> comb, ArrayList<WhiteCell> cells) {
        if (cells.size() < comb.size()) return false;
        if (comb.size() == 1) {
            int digit = comb.get(0);
            for (WhiteCell c : cells) {
                if ((!c.isEmpty() && c.getValue() == digit) || (c.isEmpty() && c.isNotationChecked(digit))) return true;
            }
            return false;
        }
        boolean success = false;
        for (int i = cells.size()-1; !success && i >= 0; i--) {
            WhiteCell c = cells.get(i);
            cells.remove(i);
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
                        cells.add(i, c);
                        return success;
                    }
                }
            }
            cells.add(i, c);
        }
        return success;
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

    public boolean generate() {
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

            // FIXME: DEBUGGING PURPOSES
            //System.out.println("-------------"+start+"----------row: "+coordRow+"------col: "+coordCol+"-------------");
            //if (start == 0 || start == 1 || start == 3 || start == 4) continue;

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
                        uniqueAssigned ++;
                        ///* FIXME: DEBUGGING PURPOSES
                         System.out.println("Assignation successful:");
                         System.out.println("Coord: " + coordRow + "," + coordCol + " Row sum: " + uniqueValue[0] + ", Col sum: " + uniqueValue[1] + ", Unique value: " + uniqueValue[2]);
                         //printNotations();
                         //*/
                    }
                    else {
                        System.out.println("Assignation FAILURE:");
                        System.out.println("Coord: " + coordRow + "," + coordCol + " Row sum: " + uniqueValue[0] + ", Col sum: " + uniqueValue[1] + ", Unique value: " + uniqueValue[2]);
                        //printNotations();*/
                        rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                        //printNotations();
                    }
                }
            }
        }
        //* FIXME: DEBUGGING PURPOSES
         System.out.println("Assigned: " + uniqueAssigned + " unique values, but tried: " + numOfStartingPoints);
         printNotations();
         //printData();
         //return;*/


        // At this point we've had a number of successful starting point assignments, now we assign the WhiteCell
        // that has the least notations (possible values) in a way that makes it non ambiguous

        int ambiguousCells = 0;
        ArrayList<Coordinates> impossibleAssignments = new ArrayList<>();

        ArrayList<Coordinates> possibleAmbiguities = new ArrayList<>();

        //int iter = 0;
        while (!notationsQueue.isEmpty()) {
            // then we have elements with no known value so we must do an assignation
            WhiteCell candidate = notationsQueue.popFirstOrderedCell(); //should never return a cell with value
            Pair<Integer, Integer> coord = candidate.getCoordinates();
            boolean isRowSumAssigned = rowSums[rowLine[coord.first][coord.second]] != 0;
            boolean isColSumAssigned = colSums[colLine[coord.first][coord.second]] != 0;
            // if one or both of the sums are not assigned, we should choose the value in
            // its notations that given the current values in the row and column there is a unique value for a
            // certain sum assignation
            //System.out.println("At iteration " + iter + ", cell has notationSize: " + workingBoard.getCellNotationSize(coord.first, coord.second));
            //iter++;

            // FIXME:: THIS IS A HOTFIX THAT CREATES TOTALLY AMBIGUOUS KAKUROS, JUST TO HAVE SOMETHING FOR THE FIRST DELIVER
            boolean[] candidateNotations = candidate.getNotations();
            ArrayList<Integer> candNot = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (candidateNotations[i]) candNot.add(i+1);
            }
            Collections.shuffle(candNot, random);
            boolean success = false;
            for (int value : candNot) {
                ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                ArrayList<Integer> colSumRollBack = new ArrayList<>();
                ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                success = cellValueAssignation(coord.first, coord.second, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                if (success) {
                    System.out.println("Successful assignation at coord: " + coord.first + ", " + coord.second);
                    break;
                } else {
                    rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }
            }
            if (!success) {
                System.out.println("FAILED assignation at coord: " + coord.first + ", " + coord.second);
                notationsQueue.removeOrderedCell(coord.first, coord.second);
                possibleAmbiguities.add(new Coordinates(coord.first, coord.second));
            }
            // FIXME:: HERE ENDS THE HOTFIX, WE NEED TO IMPLEMENT FROM HERE ON

            /*
            if (!isRowSumAssigned || !isColSumAssigned) {
                boolean success = valueBiasedSumAssignation(coord.first, coord.second, isRowSumAssigned, isColSumAssigned, -1);
                if (success) continue;
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
                boolean[] interestNotations = candidate.getNotations();
                int uniqueValue = uniqueNotationIn(coord.first, coord.second, interestNotations, true);
                boolean success = false;
                if (uniqueValue != -1) {
                    // assign the value to the cell!
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    success = cellValueAssignation(coord.first, coord.second, uniqueValue, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }
                if (!success) { // check unique in col
                    uniqueValue = uniqueNotationIn(coord.first, coord.second, interestNotations, false);
                    if (uniqueValue != -1) { // assign the value to the cell!
                        ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                        ArrayList<Integer> colSumRollBack = new ArrayList<>();
                        ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                        ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                        success = cellValueAssignation(coord.first, coord.second, uniqueValue, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                        if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    }
                }
                if (success) {
                    // let's check if our cell now has value
                    if(!workingBoard.isEmpty(coord.first, coord.second)) continue; // ambiguity solved
                }
                // at this point, either no notation is unique or assignations failed, or assignations worked but didn't solve the ambiguity
                // call function to find responsible for undoing ambiguity
                // FIXME: Something isn't working, it causes a stack overflow
                //System.out.println("Would call ambiguity solver");
                boolean[][] visited = new boolean[rows][columns];
                success = ambiguitySolver(coord.first, coord.second, visited, coord.first, coord.second, interestNotations, true);
                if (!success) success = ambiguitySolver(coord.first, coord.second, visited, coord.first, coord.second, interestNotations, false);
                if (success) {
                    // let's check if our cell now has value
                    if (!workingBoard.isEmpty(coord.first, coord.second)) continue; // ambiguity solved
                }
            }
            // seems like a possible ambiguity
            notationsQueue.removeOrderedCell(coord.first, coord.second); // the rollback mechanism will have inserted back to the queue the poped cell
            possibleAmbiguities.add(new Coordinates(coord.first, coord.second));
            /*TODO: this last part of the while of the original code should probably be removed, it's not a good option
                 to decide that it will be ambiguous now, better wait until end to see if it was resolved, if not then we accept defeat
             //unsolvable ambiguity, choose one of the values and move on with life
            boolean[] possibleValues = workingBoard.getCellNotations(coord.first, coord.second);
            boolean success = false;
            for (int i = 0; !success && i < 9; i++) {
                if (possibleValues[i]) {
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    success = cellValueAssignation(coord.first, coord.second, i+1, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }
            }
            if (!success) {
                System.out.println("UNAVOIDABLE FAILURE: This is a nightmare, no value can be assigned to this cell in ccordinates: "+coord.first+", "+coord.second);
                impossibleAssignments.add(new Coordinates(coord.first, coord.second));
            } else {
                System.out.println("UNAVOIDABLE AMBIGUITY: This board will have more than one solution because of: "+coord.first+", "+coord.second);
                ambiguousCells++;
            }*/
        }

        //System.out.println("PROBLEM REPORT: #of unavoidable failures: " + impossibleAssignments.size() + ", #of ambiguities: " + ambiguousCells);

        /*
        int ambiguitiesRemaining = 0;
        for (Coordinates c : possibleAmbiguities) {
            if (!workingBoard.isEmpty(c.r, c.c)) System.out.println("Ambiguity was solved by itself at: " + c.r + ", " + c.c);
            else {
                System.out.println("Ambiguity remaining at: " + c.r + ", " + c.c);
                ambiguitiesRemaining ++;
            }
        }
        System.out.println("Total ambiguities remaining: " + ambiguitiesRemaining);
        printNotations();
        printData();
*/
        // when we get out of the while loop we should have a filled board generated,
        // maybe we want to send it to the solver to check if it's unique or not or check for permutations, etc.
        // then:

        // FIXME:: THIS IS A PROVISIONAL FIX THAT CREATES TOTALLY AMBIGUOUS KAKUROS, JUST TO HAVE SOMETHING FOR THE FIRST DELIVER

        // IN PROVISIONAL VERSION WE ASSIGN ANY VALUE NOT IN THE ROW OR COLUMN AND WILL RE-ADAPT THE SUMS LATER
        // BECAUSE IF IT DIDN'T GET ASSIGNED IT WONT BE ABLE TO ASSIGN IT NOW EITHER. IF THE UPDATE OF POSSIBILITIES
        // MECHANISM IS WORKING WELL THIS SHOULD NEVER HAPPEN, THERE MUST ALWAYS BE AT LEAST ONE POSSIBILITY AVAILABLE
        ArrayList<Coordinates> toSolve = new ArrayList<>();
        boolean foundAmbiguous = false;
        for (Coordinates c : possibleAmbiguities) {
            if (workingBoard.isEmpty(c.r, c.c)) {
                toSolve.add(c);
                foundAmbiguous = true;
            }
        }
        if (toSolve.size() > 0) provisionalFillInBacktracking(toSolve);
        // IN FINAL VERSION WE NEED TO DECIDE IF WE FILL IT IN WITH AN AMBIGUOUS NUMBER AND LET IT AMBIGUOUS
        // OR AVOID AMBIGUITY BUT GENERATED BOARD WILL MAYBE HAVE WHITE CELLS WITH INITIALLY SET VALUES.
        // for (Coordinates c : possibleAmbiguities) {...}

        if (foundAmbiguous) { // we probably added values that do not correspond with the row and col sums
            computeRowSums();
            computeColSums();
        } else { // all values in row sums and col sums have preserved their integity correctly
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
        return foundAmbiguous;
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
        System.out.println("BACKTRACKING___TO_IMPLEMENT");
    }

    private boolean valueBiasedSumAssignation(int r, int c, boolean isRowAssigned, boolean isColAssigned, int valueOfInterest) {
        if (isRowAssigned && isColAssigned) return false; // this function only works if at least one is not assigned
        int rowID = rowLine[r][c], colID = colLine[r][c];
        boolean[] rowValues = new boolean[9];
        boolean[] colValues = new boolean[9];
        for (int i = 0; i < 9; i++) { //copies of used values to be able to modify them
            rowValues[i] = rowValuesUsed[rowID][i];
            colValues[i] = colValuesUsed[colID][i];
        }
        boolean[] interestedValues = workingBoard.getCellNotations(r, c);

        if (!isRowAssigned && isColAssigned) { //try to assign a value to row to force cell[r][c] into taking a value of its notations
            ArrayList<Integer> rowSumCandidates = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (interestedValues[i]) {
                    rowValues[i] = true;
                    ArrayList<Pair<Integer, ArrayList<Integer>>> possibilities = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValues);
                    for (Pair<Integer, ArrayList<Integer>> poss : possibilities) {
                        boolean foundCandidate = true;
                        for (int p : poss.second) {
                            // if candidate combination includes a value in notations other than the one we need it's not useful
                            if (interestedValues[p-1] && p-1 != i) foundCandidate = false;
                        }
                        if (foundCandidate) rowSumCandidates.add(poss.first);
                    }
                    rowValues[i] = false;
                }
            }
            if (rowSumCandidates.size() == 0) {
                // didn't find any candidates.  if there are still notations in cell, it should be possible to find a combination
                // unless there are too many combinations available, in that case no possibility is unique enough to force the value we need
                System.out.println("FATAL ERROR: Didn't find a rowSumAssignation, candidate has " + workingBoard.getCellNotationSize(r,c) + " notations");
                return false;
            } else {
                // let's try to make a row assignation, let's check first if we need the valueOfInterest
                boolean success = false;
                /*if (valueOfInterest != -1 && rowSumCandidates.contains(valueOfInterest)) {
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    success = rowSumAssignation(r, c, valueOfInterest, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }*/
                for(int i = 0; !success && i < rowSumCandidates.size(); i++) {
                    //if (valueOfInterest != -1 && rowSumCandidates.get(i) == valueOfInterest) continue; // we have tried it before
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    success = rowSumAssignation(r, c, rowSumCandidates.get(i), rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }
                if (success) return true;
                System.out.println("FATAL ERROR: Didn't find a rowSumAssignation after trying possibilities"); //very unlikely to happen
                return false;
            }
        } else if (isRowAssigned && !isColAssigned) {
            ArrayList<Integer> colSumCandidates = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                if (interestedValues[i]) {
                    colValues[i] = true;
                    ArrayList<Pair<Integer, ArrayList<Integer>>> possibilities = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValues);
                    for (Pair<Integer, ArrayList<Integer>> poss : possibilities) {
                        boolean foundCandidate = true;
                        for (int p : poss.second) {
                            // if candidate combination includes a value in notations other than the one we need it's not useful
                            // TODO: it could be useful if "i+1" is unique in column
                            if (interestedValues[p-1] && p-1 != i) foundCandidate = false;
                        }
                        if (foundCandidate) colSumCandidates.add(poss.first); //TODO: if two values of interest share a sum option it gets added multiple times, should be added NONE of the times
                    }
                    colValues[i] = false;
                }
            }
            if (colSumCandidates.size() == 0) {
                // didn't find any candidates. this should never happen because if there are still notations in cell, it should be possible to find a combination
                System.out.println("FATAL ERROR: Didn't find a colSumAssignation");
                return false;
            } else {
                // let's try to make a col assignation
                boolean success = false;
                if (valueOfInterest != -1 && colSumCandidates.contains(valueOfInterest)) {
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    success = rowSumAssignation(r, c, valueOfInterest, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }
                for(int i = 0; !success && i < colSumCandidates.size(); i++) {
                    if (valueOfInterest != -1 && colSumCandidates.get(i) == valueOfInterest) continue; // we have tried it before
                    ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                    ArrayList<Integer> colSumRollBack = new ArrayList<>();
                    ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                    ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                    success = colSumAssignation(r, c, colSumCandidates.get(i), rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack);
                }
                if (success) return true;
                System.out.println("FATAL ERROR: Didn't find a colSumAssignation after trying possibilities"); //very unlikely to happen
                return false;
            }
        } else {
            // tough
            // TODO: implement this scenario
            return false;
        }
    }

    private int uniqueNotationIn(int r, int c, boolean[] interestNotations, boolean isRow) {
        boolean[] uniqueNotations = new boolean[] { true, true, true, true, true, true, true, true, true };
        int ID = isRow ? rowLine[r][c] : colLine[r][c];
        int firstPos = isRow ? firstRowCoord[ID].c : firstColCoord[ID].r;
        int size = isRow ? rowSize[ID] : colSize[ID];
        for (int it = firstPos; it < firstPos+size; it++) {
            if ((isRow && it == c)||(!isRow && it == r)) continue;
            boolean[] itNotations = isRow ? workingBoard.getCellNotations(r, it) : workingBoard.getCellNotations(it, c);
            for (int i = 0; i < 9; i++) uniqueNotations[i] = uniqueNotations[i] && interestNotations[i] && !itNotations[i];
        }
        boolean uniqueFound = false;
        int uniqueValue = -1;
        for (int i = 0; !uniqueFound && i < 9; i++) {
            uniqueFound = uniqueNotations[i];
            uniqueValue = i+1;
        }
        return uniqueFound ? uniqueValue : -1;
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
                        // TODO: try to assign a value to force this notation
                        int rowA = isRow ? r : it;
                        int colA = isRow ? it : c;
                        boolean isRowAssigned = rowSums[rowLine[rowA][colA]] != 0;
                        boolean isColAssigned = colSums[colLine[rowA][colA]] != 0;
                        // FIXME: Value of interested is not the value of the cell notation but the sum that forces it
                        success = valueBiasedSumAssignation(rowA, colA, isRowAssigned, isColAssigned, i+1);
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
        //System.out.println("Board: ");
        //if (workingBoard != null) System.out.println("Board is not null");
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
