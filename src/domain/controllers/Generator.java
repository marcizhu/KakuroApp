package src.domain.controllers;

import src.domain.entities.*;
import src.utils.Pair;

import java.util.*;

/**
 * Kakuro Generator class.
 * Holds basic data and algorithms used to generate new Kakuros with selectable dimensions and difficulty
 *
 * @version 0.1.0 (20/11/2020)
 */

public class Generator {

    private Board generatedBoard;
    private Board workingBoard;
    private SwappingCellQueue notationsQueue;

    private final int rows;
    private final int columns;
    private final Difficulty difficulty;
    private final long seed;
    private final boolean forceUniqueSolution;

    private Random random;

    private int[] rowSums;                  // value of sums assigned to rowLines, 0 means not assigned
    private int[] rowSize;                  // sizes of each rowLine, always between 0 and 9
    private int[] rowValuesUsed;            // cell values used (assigned) in each rowLine, always boolean[9]
    private Coordinates[] firstRowCoord;    // coordinates to the first white cell in each rowLine
    private final int[][] rowLine;          // Pointers to position at arrays of row related data
    private int rowLineSize;                // Number of different rowLines

    private int[] colSums;                  // value of sums assigned to colLines, 0 means not assigned
    private int[] colSize;                  // sizes of each colLine, always between 0 and 9
    private int[] colValuesUsed;            // cell values used (assigned) in each colLine, always boolean[9]
    private Coordinates[] firstColCoord;    // coordinates to the first white cell in each colLine
    private final int[][] colLine;          // Pointers to position at array of colValuesUsed and colSums
    private int colLineSize;                // Number of different colLines

    /**
     * Constructor.
     * Initializes a generator to generate boards of given size and difficulty.
     * @param rows       Numbers of rows of the board to generate
     * @param columns    Number of columns of the board to generate
     * @param difficulty Difficulty of the board to generate
     */
    public Generator(int rows, int columns, Difficulty difficulty) {
        this(rows, columns, difficulty, (new Random()).nextLong(), false);
    }

    /**
     * Constructor.
     * Initializes a generator to generate boards of given size, difficulty and seed
     * @param rows       Number of rows of the board to generate
     * @param columns    Number of columns of the board to generate
     * @param difficulty Difficulty of the board to generate
     * @param seed       Seed to be used by this generator
     */
    public Generator(int rows, int columns, Difficulty difficulty, long seed) {
        this(rows, columns, difficulty, seed, false);
    }

    /**
     * Constructor.
     * Initializes a generator to generate boards of given size, difficulty and seed
     * @param rows                  Number of rows of the board to generate
     * @param columns               Number of columns of the board to generate
     * @param difficulty            Difficulty of the board to generate
     * @param forceUniqueSolution   Whether the generated board should be forced into having unique solution.
     */
    public Generator(int rows, int columns, Difficulty difficulty, boolean forceUniqueSolution) {
        this(rows, columns, difficulty, (new Random()).nextLong(), forceUniqueSolution);
    }

    /**
     * Constructor.
     * Initializes a generator to generate boards of given size, difficulty and seed
     * @param rows                  Number of rows of the board to generate
     * @param columns               Number of columns of the board to generate
     * @param difficulty            Difficulty of the board to generate
     * @param seed                  Seed to be used by this generator
     * @param forceUniqueSolution   Whether the generated board should be forced into having unique solution.
     */
    public Generator(int rows, int columns, Difficulty difficulty, long seed, boolean forceUniqueSolution) {
        this.rows = rows;
        this.columns = columns;
        this.difficulty = difficulty;
        this.seed = seed;
        this.forceUniqueSolution = forceUniqueSolution;
        rowLine = new int[rows][columns];
        colLine = new int[rows][columns];
        this.random = new Random(this.seed);
    }

    /**
     * Get generated board.
     * NOTE: This function *MUST* be called after @link Generator::generate().
     * @return the newly generated board.
     */
    public Board getGeneratedBoard() {
        return this.generatedBoard;
    }

    /**
     * Get the seed used for the generation.
     * @return the seed of the Random object used for generating the board.
     */
    public long getUsedSeed() {
        return this.seed;
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
        if (col == 1 && b.isWhiteCell(row, 0)) return false;

        if (row < height-2 && b.isBlackCell(row+2, col) && b.isWhiteCell(row+1, col)) return false;
        if (row == height-2 && b.isWhiteCell(row+1, col)) return false;
        if (row > 1 && b.isBlackCell(row-2, col) && b.isWhiteCell(row-1, col)) return false;
        if (row == 1 && b.isWhiteCell(0, col)) return false;

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
                diff = 75;
                break;
            case MEDIUM:
                diff = 60;
                break;
            case HARD:
                diff = 45;
                break;
            case EXTREME:
                diff = 30;
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
        rowValuesUsed = new int[rowLineSize];
        firstRowCoord = new Coordinates[rowLineSize];
        for (int i = 0; i < rowLineSize; i++) { //initialize data at default values
            rowSums[i] = 0;
            rowSize[i] = sizes.get(i);
            rowValuesUsed[i] = 0;
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
        colValuesUsed = new int[colLineSize];
        firstColCoord = new Coordinates[colLineSize];
        for (int i = 0; i < colLineSize; i++) { //initialize data at default values
            colSums[i] = 0;
            colSize[i] = sizes.get(i);
            colValuesUsed[i] = 0;
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
            if (rowSums[rowID] == value) return true; //the assignation has already happened, no problem
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
            if (colSums[colID] == value) return true; //the assignation has already happened, no problem
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
        if ((rowValuesUsed[rowID] & (1<<(value-1))) != 0 ||  (colValuesUsed[colID] & (1<<(value-1))) != 0 || !workingBoard.isEmpty(r, c)){
            if ((rowValuesUsed[rowID] & (1<<(value-1))) != 0 && (colValuesUsed[colID] & (1<<(value-1))) != 0 && workingBoard.getValue(r, c) == value) {
                return true; // assignation is redundant, we already had it assigned so we give it as correct
            }
            return false;
        }

        //check that adding this value doesn't make the whole sum greater than it has to be for assigned sums
        if (rowSums[rowID] != 0 || colSums[colID] != 0) {
            int rowS = 0, colS = 0;
            for (int i = 0; i < 9; i++) {
                if ((rowValuesUsed[rowID] & (1<<i)) != 0) rowS += (i+1);
                if ((colValuesUsed[colID] & (1<<i)) != 0) colS += (i+1);
            }
            if ((rowSums[rowID] != 0 && rowS+value > rowSums[rowID]) || (colSums[colID] != 0 && colS+value > colSums[colID]))
                return false; //new sum would be greater than it should
        }

        cellValueRollBack.add(new Coordinates(r, c)); //if rollback we clear this coordinates and insert in notationsQueue
        if (workingBoard.getCellNotationSize(r, c) > 1) { //cell notations should be removed (important in ambiguity checking), this won't be checked before then.
            int cellNotations = workingBoard.getCellNotations(r, c);
            //int rollbackNotations = cellNotations; // IMPORTANT! rollback notations should be a new object
            // because cellNotations might get modified if we have to erase, rollback holds the original values
            ArrayList<Integer> toErase = new ArrayList<>(); //new ArrayList<>();
            for (int i = 0; i < 9; i++)
                if(i+1 != value && (cellNotations & (1<<i)) != 0) toErase.add(i + 1); // if it's not the value
            if (notationsQueue.isHiding(r, c)) hidingCellNotationsRollBack.add(new RollbackNotations(r, c, cellNotations));
            else cellNotationsRollBack.add(new RollbackNotations(r, c, cellNotations));
            notationsQueue.eraseNotationsFromCell(r, c, toErase);
        }
        notationsQueue.removeOrderedCell(r, c); // removes it from queue but notations are mantained
        workingBoard.setCellValue(r, c, value);
        rowValuesUsed[rowID] |= 1 << (value-1);
        colValuesUsed[colID] |= 1 << (value-1);
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

        ArrayList<Integer> possibleCases;

        if (rowSums[rowID] != 0) { // row sum is assigned
            // get possible cases for the row
            possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(rowSize[rowID], rowSums[rowID], rowValuesUsed[rowID]);

        } else { // row sum is NOT assigned
            // get possible cases for the row
            ArrayList<Pair<Integer, Integer>> multiplePossibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValuesUsed[rowID]);
            possibleCases = new ArrayList<>();
            int onlySum = -1;
            for (Pair<Integer, Integer> p : multiplePossibleCases) {
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
            int pCase = possibleCases.get(i);
            ArrayList<Integer> p = new ArrayList<>();
            for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
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

        int rowOptions = 0;
        for(int p : possibleCases) rowOptions |= p;

        // subtract the already used values
        int commonRowNotations = rowOptions & ~rowValuesUsed[rowID];
        boolean superPermissive = commonRowNotations == 0b111111111;

        ArrayList<ArrayList<Integer>> allToErase = new ArrayList<>();
        int minRowNotSize = 10;
        for(int it = 0; it < rowSize[rowID]; it++) {
            allToErase.add(it, new ArrayList<>());
            if (workingBoard.isEmpty(r, it+firstRowCoord[rowID].c)){
                int s = workingBoard.getCellNotationSize(r, it+firstRowCoord[rowID].c);
                if (s < minRowNotSize) minRowNotSize = s;
            }
        }
        if (minRowNotSize < rowSize[rowID]) {
            int num_cells = rowSize[rowID];
            int[] size = new int[num_cells];
            int[] notations = new int[num_cells];
            int[] insertPtrs = new int[num_cells];
            for(int it = 0; it < num_cells; it++) {
                insertPtrs[it] = it;
                size[it] = 0;
                notations[it] = 0;
                for (int i = 0; i < 9; i++) {
                    if (workingBoard.cellHasNotation(r, it + firstRowCoord[rowID].c, i+1)) {
                        notations[it] |= (1<<i);
                        size[it]++;
                    }
                }
            }
            deepNotationAnalysis(num_cells, size, notations, insertPtrs, allToErase);
        }

        // check for each non-set white-cell if its notations have some notation that is not in commonRowNotations
        // if so, erase notations, mark column as affected, add cell notations to rollback

        for(int it = firstRowCoord[rowID].c; !superPermissive && it < firstRowCoord[rowID].c+rowSize[rowID]; it++) {
            if (workingBoard.isEmpty(r, it)) { //value not set
                int cellNotations = workingBoard.getCellNotations(r, it);
                // because cellNotations might get modified if we have to erase, rollback holds the original values
                ArrayList<Integer> toErase = allToErase.get(it - firstRowCoord[rowID].c); //new ArrayList<>();
                int valuesToErase = cellNotations & ~commonRowNotations;
                for (int i = 0; i < 9; i++)
                    if((valuesToErase & (1<<i)) != 0) toErase.add(i + 1); // if it isn't part of the validated possible notations for the row

                if (toErase.size() > 0) { // we need to erase some notations
                    modifiedRows[rowID] = true;
                    modifiedCols[colLine[r][it]] = true;
                    affectedColumns.add(it);
                    if (notationsQueue.isHiding(r, it)) hidingCellNotationsRollBack.add(new RollbackNotations(r, it, cellNotations));
                    else cellNotationsRollBack.add(new RollbackNotations(r, it, cellNotations));
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
                int cellNotations = workingBoard.getCellNotations(r, affected);
                for (int i = 0; value == -1 && i < 9; i++) if((cellNotations&(1<<i)) != 0) value = i+1;
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

        ArrayList<Integer> possibleCases;

        if (colSums[colID] != 0) { // col sum is assigned
            // get possible cases for the col
            possibleCases = KakuroConstants.INSTANCE.getPossibleCasesWithValues(colSize[colID], colSums[colID], colValuesUsed[colID]);

        } else { // col sum is NOT assigned
            // get possible cases for the col
            ArrayList<Pair<Integer, Integer>> multiplePossibleCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValuesUsed[colID]);
            possibleCases = new ArrayList<>();
            int onlySum = -1;
            for (Pair<Integer, Integer> p : multiplePossibleCases) {
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
            int pCase = possibleCases.get(i);
            ArrayList<Integer> p = new ArrayList<>();
            for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
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

        int colOptions = 0;
        for(int p : possibleCases) colOptions |= p;

        // subtract the already used values
        int commonColNotations = colOptions & ~colValuesUsed[colID];
        boolean superPermissive = commonColNotations == 0b111111111;

        ArrayList<ArrayList<Integer>> allToErase = new ArrayList<>();
        int minColNotSize = 10;
        for(int it = 0; it < colSize[colID]; it++) {
            allToErase.add(it, new ArrayList<>());
            if (workingBoard.isEmpty(it + firstColCoord[colID].r, c)){
                int s = workingBoard.getCellNotationSize(it + firstColCoord[colID].r, c);
                if (s < minColNotSize) minColNotSize = s;
            }
        }
        if (minColNotSize < colSize[colID]) {
            int num_cells = colSize[colID];
            int[] size = new int[num_cells];
            int[] notations = new int[num_cells];
            int[] insertPtrs = new int[num_cells];
            for(int it = 0; it < colSize[colID]; it++) {
                insertPtrs[it] = it;
                size[it] = 0;
                notations[it] = 0;
                for (int i = 0; i < 9; i++) {
                    if (workingBoard.cellHasNotation(it + firstColCoord[colID].r, c, i+1)) {
                        notations[it] |= (1<<i);
                        size[it]++;
                    }
                }
            }
            deepNotationAnalysis(num_cells, size, notations, insertPtrs, allToErase);
        }

        for(int it = firstColCoord[colID].r; !superPermissive && it < firstColCoord[colID].r+colSize[colID]; it++) {
            if (workingBoard.isEmpty(it, c)) { //value not set
                int cellNotations = workingBoard.getCellNotations(it, c);
                // because cellNotations might get modified if we have to erase, rollback holds the original values
                ArrayList<Integer> toErase = allToErase.get(it - firstColCoord[colID].r);
                int valuesToErase = cellNotations & ~commonColNotations;
                for (int i = 0; i < 9; i++)
                    if((valuesToErase & (1<<i)) != 0) toErase.add(i + 1); // if it isn't part of the validated possible notations for the row

                if (toErase.size() > 0) { // we need to erase some notations
                    modifiedRows[rowLine[it][c]] = true;
                    modifiedCols[colID] = true;
                    affectedRows.add(it);
                    if (notationsQueue.isHiding(it, c)) hidingCellNotationsRollBack.add(new RollbackNotations(it, c, cellNotations));
                    else cellNotationsRollBack.add(new RollbackNotations(it, c, cellNotations));
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
                int cellNotations = workingBoard.getCellNotations(affected, c);
                for (int i = 0; value == -1 && i < 9; i++) if((cellNotations&(1<<i)) != 0) value = i+1;
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

    // Pre: size, notations, insertPtrs and result have size num_cells, all arrays in results are declared.
    private void deepNotationAnalysis(int num_cells, int[] size, int[] notations, int[] insertPtrs, ArrayList<ArrayList<Integer>> result) {
        if (num_cells == 0) return;

        // we order the cell info by size.
        for (int i = 0; i < num_cells; i++) { //selection sort because maximum there are 9 cells
            int minPos = i;
            for (int j = i+1; j < num_cells; j++)
                if (size[j] < size[minPos]) minPos = j;
            if (minPos != i) { // swap
                int swpSize, swpNts, swpPtrs;
                swpSize = size[i]; swpNts = notations[i]; swpPtrs = insertPtrs[i];
                size[i] = size[minPos]; notations[i] = notations[minPos]; insertPtrs[i] = insertPtrs[minPos];
                size[minPos] = swpSize; notations[minPos] = swpNts; insertPtrs[minPos] = swpPtrs;
            }
        }

        int ini = 0;
        while(ini < num_cells && size[ini] < 2) ini++;

        if (ini < num_cells)
        for (int length = size[ini]; length < num_cells; length++) {
            int end = num_cells-1;
            while (end >= ini && size[end] > length) end--;

            if (end - ini +1 < length) continue;

            int[] indices = new int[length];
            for (int j = 0; j < length; j++) {
                indices[j] = ini+j;
            }

            boolean atEnd = false;
            while (!atEnd) {
                // consider this possibility
                int poss = 0;
                for (int j : indices)
                    poss |= notations[j];

                int checkSize = poss, nSize = 0;
                while (checkSize > 0) {
                    nSize += (checkSize & 1);
                    checkSize >>>= 1;
                }

                if (nSize == length) { // these |length| values have to be in the corresponding cells, not in others
                    ArrayList<Integer> toErase = new ArrayList<>();
                    for (int j = 0; toErase.size() < length && j < 9; j++) {
                        if ((poss & (1<<j)) > 0) toErase.add(j);
                    }
                    int minLengthChanged = -1;
                    int kIdx = 0;
                    for (int j = 0; j < num_cells; j++) {
                        if (kIdx < length-1 && j > indices[kIdx]) kIdx++;
                        if (j != indices[kIdx]) {
                            for (int val : toErase) {
                                if ((notations[j] & (1<<val)) > 0) { // has a value to be erased
                                    result.get(insertPtrs[j]).add(val+1); //mark it to erase
                                    notations[j] &= ~(1<<val); // erase from notations to calc.
                                    size[j]--;
                                    if (minLengthChanged == -1 || size[j] < minLengthChanged) minLengthChanged = size[j];
                                }
                            }
                        }
                    }

                    // if any changes, reorganize and check the new lengths again, some might have new unique values.
                    if (minLengthChanged >= 0) {
                        for (int i = 0; i < num_cells; i++) { //selection sort because maximum there are 9 cells
                            int minPos = i;
                            for (int j = i+1; j < num_cells; j++)
                                if (size[j] < size[minPos]) minPos = j;
                            if (minPos != i) { // swap
                                int swpSize, swpNts, swpPtrs;
                                swpSize = size[i]; swpNts = notations[i]; swpPtrs = insertPtrs[i];
                                size[i] = size[minPos]; notations[i] = notations[minPos]; insertPtrs[i] = insertPtrs[minPos];
                                size[minPos] = swpSize; notations[minPos] = swpNts; insertPtrs[minPos] = swpPtrs;
                            }
                        }

                        if (minLengthChanged < 2) {
                            ini = 0;
                            while(ini < num_cells && size[ini] < 2) ini++;
                            length = size[ini];
                            break;
                        } else if (minLengthChanged < length) {
                            length = minLengthChanged;
                            break;
                        }
                    }
                }

                if (indices[0] == end - (length-1)) atEnd = true;
                for (int i = 1; !atEnd && i < length; i++) {
                    if (indices[i] == end - ((length-1) - i)) {
                        indices[i-1]++;
                        indices[i] = indices[i-1] + 1;
                        if (indices[i] != end - ((length-1) - i)) {
                            // idx was moved, must take the rest with him
                            for (int j = i+1; j < length; j++) indices[j] = indices[j-1]+1;
                        }
                        break;
                    } else if (i == length-1) {
                        indices[i]++;
                    }
                }
            }
        }
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
            rowValuesUsed[rowLine[c.r][c.c]] &= ~(1<<(value-1));
            colValuesUsed[colLine[c.r][c.c]] &= ~(1<<(value-1));
        }
        // Cell notations
        // notice that it is important to first insert the cells if needed, because if we don't the datastructure
        // will not consider it as valid and it won't find it to add the notations
        for (RollbackNotations n : cellNotationsRollBack) {
            Coordinates c = n.coord;
            ArrayList<Integer> toAdd = new ArrayList<>();
            for (int i = 0; i < 9; i++) if ((n.notations & (1<<i)) != 0) toAdd.add(i+1);
            notationsQueue.addNotationsToCell(c.r, c.c, toAdd);
        }
        for (RollbackNotations n : hidingCellNotationsRollBack) {
            Coordinates c = n.coord;
            ArrayList<Integer> toAdd = new ArrayList<>();
            for (int i = 0; i < 9; i++) if ((n.notations & (1<<i)) != 0) toAdd.add(i+1);
            notationsQueue.addNotationsToCell(c.r, c.c, toAdd);
            notationsQueue.hideElement(c.r, c.c);
        }
    }

    /**
     * Generate a new board.
     * This function generates the new board using the given dimensions, difficulty and (optionally) seed.
     */
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
                if (generateStartingPoint(coordRow, coordCol, workingBoard.getCellNotations(coordRow, coordCol))) uniqueAssigned++;
            }
        }

        // At this point we've had a number of successful starting point assignments, now we assign the WhiteCell
        // that has the least notations (possible values) in a way that makes it non ambiguous
        TreeSet<Coordinates> possibleAmbiguities = new TreeSet<>();
        resolveEnqueuedCellValues(possibleAmbiguities);

        //System.out.println("Before ambiguity check");
        //printNotations();
        // After assigning as many cells as possible we check for existing ambiguities and resolve them
        TreeSet<Coordinates> forcedValues = new TreeSet<>();
        TreeSet<Coordinates> realAmbiguities = new TreeSet<>(); // should never be used if everything is okay
        boolean finished = false;
        while (!finished) {
            finished = true;
            for (Coordinates c : possibleAmbiguities) {
                if (workingBoard.isEmpty(c.r, c.c)) { // still ambiguous, assign one of the values...
                    ArrayList<Integer> valuesToTry = new ArrayList<>();
                    for (int i = 0; i < 9; i++) if (workingBoard.cellHasNotation(c.r, c.c, i+1)) valuesToTry.add(i+1);
                    Collections.shuffle(valuesToTry, random);
                    for (int value : valuesToTry) {
                        ArrayList<Integer> rowSumRollBack = new ArrayList<>();
                        ArrayList<Integer> colSumRollBack = new ArrayList<>();
                        ArrayList<Coordinates> cellValueRollBack = new ArrayList<>();
                        ArrayList<RollbackNotations> cellNotationsRollBack = new ArrayList<>();
                        ArrayList<RollbackNotations> hidingCellNotationsRollBack = new ArrayList<>();
                        boolean[] modifiedRows = new boolean[rowLineSize]; //default to false
                        boolean[] modifiedCols = new boolean[colLineSize]; //default to false
                        boolean success = cellValueAssignation(c.r, c.c, value, rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack, modifiedRows, modifiedCols);
                        if (success) {
                            if (forceUniqueSolution) forcedValues.add(c);
                            finished = false;
                            break;
                        } else {
                            rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
                        }
                    }
                    if (workingBoard.isEmpty(c.r, c.c)) realAmbiguities.add(c); //this should never happen
                    if (!finished) break; // a value was assigned, should check the correct way of assigning values before continuing.
                }
            }
            if (!notationsQueue.isEmpty()) resolveEnqueuedCellValues(possibleAmbiguities);
        }
        //System.out.println("After ambiguity check");
        //printNotations();

        if (realAmbiguities.size() > 0) { // this should never happen
            System.out.println("THIS SHOULDN'T HAPPEN!!! Cells are left without options... Unique solution can't be guaranteed");
            ArrayList<Coordinates> toSolve = new ArrayList<>();
            for (Coordinates c : realAmbiguities)
                if (workingBoard.isEmpty(c.r, c.c)) toSolve.add(c);
            provisionalFillInBacktracking(toSolve);
            computeRowSums();
            computeColSums();
        } else {
            System.out.println("Unique solution found!");
            defineBlackCellSums();
        }

        // Finally, after resolving all ambiguities we proceed to create the generated board.
        generatedBoard = new Board(columns, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isBlackCell(i, j)) {
                    generatedBoard.setCell(new BlackCell((BlackCell)workingBoard.getCell(i, j)), i, j);
                } else {
                    if (forcedValues.contains(new Coordinates(i, j))) generatedBoard.setCell(new WhiteCell(workingBoard.getValue(i, j)), i, j);
                    else generatedBoard.setCell(new WhiteCell(), i, j);
                }
            }
        }
    }

    private boolean generateStartingPoint (int coordRow, int coordCol, int interestedValues) {
        ArrayList<int[]> uniqueCrossValues = KakuroConstants.INSTANCE.getUniqueCrossValues(rowSize[rowLine[coordRow][coordCol]], colSize[colLine[coordRow][coordCol]], difficulty); // returns [] of {rowSum, colSum, valueInCommon}
        for (int i = 0; i < uniqueCrossValues.size(); i++) {
            int[] uniqueValue = uniqueCrossValues.get(i);
            if ((interestedValues & (1<<(uniqueValue[2]-1))) == 0) continue;
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

    private void resolveEnqueuedCellValues(TreeSet<Coordinates> possibleAmbiguities) {
        int iter = 0;
        while (!notationsQueue.isEmpty()) {
            iter++;
            //System.out.println("Notations at iter: " + iter);
            //printNotations();
            //System.out.println();
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
                        System.out.println("Success at valueBiasedSumAssig but no value was assigned at iter: " + iter + ", coord: " + coord.first + "," + coord.second);
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
            else if (candidate.getNotationSize() == 2) {
                // call function to find responsible for undoing ambiguity
                boolean success = false;

                int[][] visited = new int[rows][columns];
                for (int i = 0; !success && i < 9; i++)  //only two iterations are actually useful
                    if (candidate.isNotationChecked(i+1))
                        success = ambiguitySolver(coord.first, coord.second, i+1, visited);

                if (success) {
                    // let's check if our cell now has value
                    if (!workingBoard.isEmpty(coord.first, coord.second)) {
                        notationsQueue.removeOrderedCell(coord.first, coord.second);
                        continue; // ambiguity solved
                    } else {
                        System.out.println("Wasnt avoided but did success... shouldnt happen at iter: " + iter + ", coord: " + coord.first + "," + coord.second);
                    }
                }
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
        Collections.shuffle(possibleValues, random);

        for (int val: possibleValues) {
            int currentVal = workingBoard.getValue(r, c);
            int currentUsedR = rowValuesUsed[rowID];
            int currentUsedC = colValuesUsed[colID];

            workingBoard.setCellValue(r, c, val);
            rowValuesUsed[rowID] |= 1<<(val-1);
            colValuesUsed[colID] |= 1<<(val-1);

            if (fillWhiteCells(coord, pos+1)) return true;

            if (currentVal == 0) workingBoard.clearCellValue(r, c);
            else workingBoard.setCellValue(r, c, currentVal);
            rowValuesUsed[rowID] = currentUsedR;
            colValuesUsed[colID] = currentUsedC;
        }

        return false;
    }

    private ArrayList<Integer> notUsedValues(int rowID, int colID) {
        // Returns a list of the values that have not been used in the row and column
        ArrayList<Integer> values = new ArrayList<>();

        int allValues = ~rowValuesUsed[rowID] & ~colValuesUsed[colID];
        for(int i = 1; i<=9; i++)
            if ((allValues & (1<<(i-1))) != 0) values.add(i);

        return values;
    }

    private boolean valueBiasedSumAssignation(int r, int c, int interestedValues) {
        boolean isRowAssigned = rowSums[rowLine[r][c]] != 0;
        boolean isColAssigned = colSums[colLine[r][c]] != 0;
        if (isRowAssigned && isColAssigned) return false; // this function only works if at least one is not assigned
        int rowID = rowLine[r][c], colID = colLine[r][c];

        if (!isRowAssigned && isColAssigned) { //try to assign a value to row to force cell[r][c] into taking a value of its notations
            int rowValues = rowValuesUsed[rowID];
            int uniqueNotations = uniqueNotationsIn(r, c, workingBoard.getCellNotations(r, c), true);
            TreeSet<Integer> rowSumCandidates = new TreeSet<>();
            TreeSet<Integer> rowSumCandidatesRepeated = new TreeSet<>();
            for (int i = 0; i < 9; i++) {
                if (workingBoard.cellHasNotation(r, c, i+1)) {
                    rowValues |= 1<<i;
                    ArrayList<Pair<Integer, Integer>> possibilities = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValues);

                    for (int k = possibilities.size()-1; k >= 0; k--) {
                        int pCase = possibilities.get(k).second;
                        ArrayList<Integer> p = new ArrayList<>();
                        for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
                        ArrayList<WhiteCell> containingCells = new ArrayList<>();
                        for (int it = firstRowCoord[rowID].c; it < firstRowCoord[rowID].c+rowSize[rowID]; it++) {
                            for (int digit : p) {
                                if ((!workingBoard.isEmpty(r, it) && workingBoard.getValue(r, it) == digit) || workingBoard.cellHasNotation(r, it, digit)) {
                                    containingCells.add((WhiteCell)workingBoard.getCell(r, it));
                                    break;
                                }
                            }
                        }
                        if (!isCombinationPossible(p, containingCells)) possibilities.remove(k);
                    }

                    for (Pair<Integer, Integer> poss : possibilities) {
                        boolean foundCandidate = true;
                        if ((uniqueNotations & (1<<i)) == 0) { //if i is a unique notation, any combination that contains it is useful
                            for (int j = 0; j < 9; j++) {
                                if ((poss.second & (1<<j)) == 0) continue;
                                // if candidate combination includes a value in notations other than the one we need it's not useful
                                if (workingBoard.cellHasNotation(r, c, j+1) && j != i)foundCandidate = false;
                            }
                        }
                        if (foundCandidate) {
                            if (rowSumCandidates.contains(poss.first) || (interestedValues & (1<<i)) == 0) rowSumCandidatesRepeated.add(poss.first);
                            else rowSumCandidates.add(poss.first);
                        }
                    }
                    rowValues &= ~(1<<i);
                }
            }
            rowSumCandidates.removeAll(rowSumCandidatesRepeated);
            if (rowSumCandidates.size() == 0) {
                // didn't find any candidates.  if there are still notations in cell, it should be possible to find a combination
                // unless there are too many combinations available, in that case no possibility is unique enough to force the value we need
                return false;
            } else {
                // let's try to make a row assignation
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
                    if (success) success = !workingBoard.isEmpty(r, c);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
                    else break;
                }
                if (success) return true;
                return false;
            }
        } else if (isRowAssigned && !isColAssigned) {
            int colValues = colValuesUsed[colID];
            int uniqueNotations = uniqueNotationsIn(r, c, workingBoard.getCellNotations(r, c), false);
            TreeSet<Integer> colSumCandidates = new TreeSet<>();
            TreeSet<Integer> colSumCandidatesRepeated = new TreeSet<>();
            for (int i = 0; i < 9; i++) {
                if (workingBoard.cellHasNotation(r, c, i+1)) {
                    colValues |= 1<<i;
                    ArrayList<Pair<Integer, Integer>> possibilities = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValues);

                    for (int k = possibilities.size()-1; k >= 0; k--) {
                        int pCase = possibilities.get(k).second;
                        ArrayList<Integer> p = new ArrayList<>();
                        for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
                        ArrayList<WhiteCell> containingCells = new ArrayList<>();
                        for (int it = firstColCoord[colID].r; it < firstColCoord[colID].r+colSize[colID]; it++) {
                            for (int digit : p) {
                                if ((!workingBoard.isEmpty(it, c) && workingBoard.getValue(it, c) == digit) || workingBoard.cellHasNotation(it, c, digit)) {
                                    containingCells.add((WhiteCell)workingBoard.getCell(it, c));
                                    break;
                                }
                            }
                        }
                        if (!isCombinationPossible(p, containingCells)) possibilities.remove(k);
                    }

                    for (Pair<Integer, Integer> poss : possibilities) {
                        boolean foundCandidate = true;
                        if ((uniqueNotations & (1<<i)) == 0) { //if i is a unique notation, any combination that contains it is useful
                            for (int j = 0; j < 9; j++) {
                                if ((poss.second & (1<<j)) == 0) continue;
                                // if candidate combination includes a value in notations other than the one we need it's not useful
                                if (workingBoard.cellHasNotation(r, c, j+1) && j != i)foundCandidate = false;
                            }
                        }
                        if (foundCandidate) {
                            if (colSumCandidates.contains(poss.first) || (interestedValues & (1<<i)) == 0) colSumCandidatesRepeated.add(poss.first);
                            else colSumCandidates.add(poss.first);
                        }
                    }
                    colValues &= ~(1<<i);
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
                    if (success) success = !workingBoard.isEmpty(r, c);
                    if (!success) rollBack(rowSumRollBack, colSumRollBack, cellValueRollBack, cellNotationsRollBack, hidingCellNotationsRollBack);
                    else break;
                }
                if (success) return true;
                return false;
            }
        } else {
            // if no values are assigned to the row and column then we choose a new starting point (if we can)
            boolean shouldCreateStartingPoint = rowValuesUsed[rowID] == 0 && colValuesUsed[colID] == 0;

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
                return generateStartingPoint(r, c, interestedValues);
            } else {
                // some values are assigned, take them into consideration in the search

                // GET THE ROW POSSIBILITIES
                ArrayList<Pair<Integer, Integer>> rowCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(rowSize[rowID], rowValuesUsed[rowID]);
                // validate combinations or erase if not valid
                for (int i = rowCases.size()-1; i >= 0; i--) {
                    int pCase = rowCases.get(i).second;
                    ArrayList<Integer> p = new ArrayList<>();
                    for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
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
                ArrayList<Pair<Integer, Integer>> colCases = KakuroConstants.INSTANCE.getPossibleCasesUnspecifiedSum(colSize[colID], colValuesUsed[colID]);
                // validate combinations or erase if not valid
                for (int i = colCases.size()-1; i >= 0; i--) {
                    int pCase = colCases.get(i).second;
                    ArrayList<Integer> p = new ArrayList<>();
                    for (int j = 0; j < 9; j++) if ((pCase & (1<<j)) != 0) p.add(j+1);
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
                    int rowValuesSeen = 0;
                    int rowSumOpt = rowCases.get(i).first;
                    while(i < rowCases.size() && rowCases.get(i).first == rowSumOpt) {
                        rowValuesSeen |= rowCases.get(i).second;
                        i++;
                    }

                    for (int j = 0; j < colCases.size(); ) {
                        int colValuesSeen = 0;
                        int colSumOpt = colCases.get(j).first;
                        while(j < colCases.size() && colCases.get(j).first == colSumOpt) {
                            colValuesSeen |= colCases.get(j).second;
                            j++;
                        }

                        int possibleUniqueCrossValues = rowValuesSeen & colValuesSeen;
                        if (Integer.bitCount(possibleUniqueCrossValues) == 1 && (interestedValues & possibleUniqueCrossValues) != 0)
                            uniqueSumCombination.add(new Pair<> (rowSumOpt, colSumOpt));
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

    private int uniqueNotationsIn(int r, int c, int interestNotations, boolean isRow) {
        int uniqueNotations = 0b111111111;
        int ID = isRow ? rowLine[r][c] : colLine[r][c];
        int firstPos = isRow ? firstRowCoord[ID].c : firstColCoord[ID].r;
        int size = isRow ? rowSize[ID] : colSize[ID];
        for (int it = firstPos; it < firstPos+size; it++) {
            if ((isRow && it == c)||(!isRow && it == r)) continue;
            int itNotations = isRow ? workingBoard.getCellNotations(r, it) : workingBoard.getCellNotations(it, c);
            uniqueNotations &= (interestNotations & ~itNotations);
        }
        return uniqueNotations;
    }

    // to call when only two values are possible and want to get rid of one of them, interest is in [1..9]
    private boolean ambiguitySolver(int r, int c, int interest, int[][] visited) {
        if (((visited[r][c] >> interest-1) & 1) > 0) return false; // already seen this and didn't solve anything
        visited[r][c] |= (1 << interest-1);
        int rowID = rowLine[r][c];
        // check both row and column for new value
        for (int it = firstRowCoord[rowID].c; it < firstRowCoord[rowID].c+rowSize[rowID]; it++) {
            if (it != c && workingBoard.cellHasNotation(r, it, interest)) {
                // try to assign it the interest value
                if (colSums[colLine[r][it]] == 0 && rowSums[rowLine[r][it]] != 0) {
                    if (valueBiasedSumAssignation(r, it, 1<<(interest-1))) return true;
                }
                // if it has 2 notations, look for the other one and try to make another cell take that other value.
                if (workingBoard.getCellNotationSize(r, it) == 2) {
                    for (int i = 0; i < 9; i++) {
                        if (workingBoard.cellHasNotation(r, it, i+1) && i+1!=interest) {
                            if (ambiguitySolver(r, it, i+1, visited)) return true;
                        }
                    }
                }
            }
        }
        int colID = colLine[r][c];
        for (int it = firstColCoord[colID].r; it < firstColCoord[colID].r+colSize[colID]; it++) {
            if (it != r && workingBoard.cellHasNotation(it, c, interest)) {
                // try to assign it the interest value
                if (rowSums[rowLine[it][c]] == 0 && colSums[colLine[it][c]] != 0) {
                    if (valueBiasedSumAssignation(it, c, 1<<(interest-1))) return true;
                }
                // if it has 2 notations, look for the other one and try to make another cell take that other value.
                if (workingBoard.getCellNotationSize(it, c) == 2) {
                    for (int i = 0; i < 9; i++) {
                        if (workingBoard.cellHasNotation(it, c, i+1) && i+1!=interest) {
                            if (ambiguitySolver(it, c, i+1, visited)) return true;
                        }
                    }
                }
            }
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
                        int b = workingBoard.getCellNotations(i, j);
                        System.out.print("[");
                        for (int k = 0; k < 9; k++) {
                            if ((b&(1<<k)) != 0) System.out.print((k+1)+"");
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

        /**
         * Constructor
         * @param r Row coordinate
         * @param c Column coordinate
         */
        public Coordinates(int r, int c) {
            this.r = r;
            this.c = c;
        }

        /**
         * Comparison operator
         * @param o Object to compare with
         * @return whether the compared objects are smaller, equal or greater
         */
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
        public int notations;
        public RollbackNotations(int r, int c, int n) {
            coord = new Coordinates(r, c);
            notations = n;
        }
    }
}
