package src.domain.algorithms;

import src.domain.algorithms.helpers.KakuroConstants;
import src.domain.algorithms.helpers.KakuroFunctions;
import src.domain.algorithms.helpers.SwappingCellQueue;
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
    private KakuroFunctions assigFunctions;

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

        // First column always black
        for (int i = 0; i < height; i++) b.setCell(new BlackCell(), i, 0);

        // First row always black
        for (int j = 0; j < width; j++) b.setCell(new BlackCell(), 0, j);

        int lastIdx = ((width-1)*(height-1))/2;
        lastIdx += width + (lastIdx/(width-1)) +1;
        for (int idx = width+1; idx <= lastIdx; idx++) {
            int i = idx/width;
            int j = idx%width;
            int symI = height-i;
            int symJ = width-j;
            if (i == 0 || j == 0) continue;
            Cell c = new WhiteCell(true);
            if ((Math.abs(random.nextInt(100)) < diff && isValidPosition(b, i, j))) {
                // Cell will be black if we are in the first row or column or randomly with a 1/7 chance
                if (!((symI-i == 2 && j == symJ && (i+1 < height && b.isWhiteCell(i+1, j))) || (i == symI && symJ-j == 2 && (j+1 < width && b.isWhiteCell(i, j+1))))) {
                    if (checkConnected(i, j, b)) c = new BlackCell();
                }
            }
            b.setCell(c, i, j);
            if (c instanceof BlackCell) b.setCell(new BlackCell((BlackCell) c), symI, symJ);
            else b.setCell(new WhiteCell(true), symI, symJ);
        }

        // Traverse the board once and fix all rows and columns of length > 9
        for(int i = 0; i<height; i++) {
            for (int j = 0; j < width; j++) {
                if (b.isWhiteCell(i, j)) continue;
                if (((i+1 < height && b.isBlackCell(i+1, j)) || i+1 >= height) && ((j+1 < width && b.isBlackCell(i, j+1)) || j+1 >= width)) continue;
                // At this point this black cell is a starting point for either a row or a column
                // check row
                if (j+1 < width && b.isWhiteCell(i, j+1)) makeNecessaryPartitionRow(i, j, b);
                // check column
                if (i+1 < height && b.isWhiteCell(i+1, j)) makeNecessaryPartitionCol(i, j, b);
            }
        }
        return b;
    }

    private boolean checkConnected (int i, int j, Board b) {
        int height = b.getHeight();
        int width = b.getWidth();
        int symI = height-i;
        int symJ = width-j;
        ArrayList<Coordinates> toVisit = new ArrayList<>();
        for (int k = 0; k < 3; k++) {
            for (int kk = 0; kk < 3; kk++) {
                if (i-1+k < 0 || i-1+k >= height || j-1+kk < 0 || j-1+kk >= width) continue;
                if ((i-1+k != i || j-1+kk != j) && (i-1+k != symI || j-1+kk != symJ) && b.isWhiteCell(i-1+k, j-1+kk)) toVisit.add(new Coordinates( i-1+k, j-1+kk));
            }
        }
        if (toVisit.size() > 0) {
            boolean[][] visited = new boolean[height][width];
            visited[i][j] = true;
            visited[symI][symJ] = true;
            // Probably should be done iteratively BFS
            PriorityQueue<Pair<Integer, Coordinates>> queue = new PriorityQueue(1, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    int f = ((Pair<Integer, Coordinates>) o1).first;
                    int s = ((Pair<Integer, Coordinates>) o2).first;
                    if (f == s) return 0;
                    if (f < s) return -1;
                    return 1;
                }
            });
            queue.add(new Pair<>(gridDist(i,j,toVisit.get(0).r,toVisit.get(0).c), toVisit.get(0)));
            while (!queue.isEmpty() && !toVisit.isEmpty()) {
                Coordinates currPos = queue.poll().second;
                if (visited[currPos.r][currPos.c]) continue;
                visited[currPos.r][currPos.c] = true;
                toVisit.remove(currPos);
                // Check adjacent whiteCells
                if (currPos.r-1 >= 0 && b.isWhiteCell(currPos.r-1, currPos.c)) queue.add(new Pair<>(gridDist(i,j,currPos.r-1,currPos.c), new Coordinates(currPos.r-1,currPos.c)));
                if (currPos.r+1 < height && b.isWhiteCell(currPos.r+1, currPos.c)) queue.add(new Pair<>(gridDist(i,j,currPos.r+1,currPos.c), new Coordinates(currPos.r+1,currPos.c)));
                if (currPos.c-1 >= 0 && b.isWhiteCell(currPos.r, currPos.c-1)) queue.add(new Pair<>(gridDist(i,j,currPos.r,currPos.c-1), new Coordinates(currPos.r,currPos.c-1)));
                if (currPos.c+1 < width && b.isWhiteCell(currPos.r, currPos.c+1)) queue.add(new Pair<>(gridDist(i,j,currPos.r,currPos.c+1), new Coordinates(currPos.r,currPos.c+1)));
            }
            return toVisit.isEmpty();
        }
        return true; // shouldn't happen, would mean we already had more than one connected component and cell was surrounded by black cells
    }

    private int gridDist(int i, int j, int r, int c) {
        int xDist = i<r ? r-i : i-r;
        int yDist = j<c ? c-j : j-c;
        return xDist + yDist;
    }

    private void makeNecessaryPartitionRow(int i, int j, Board b) {
        int height = b.getHeight();
        int width = b.getWidth();
        int pos = j+1;
        int size = 0;
        ArrayList<Integer> validPos = new ArrayList<>();
        ArrayList<Integer> invalidPos = new ArrayList<>();
        ArrayList<Integer> allPos = new ArrayList<>();
        while (pos < width && b.isWhiteCell(i, pos)) {
            size++;
            int symI = height-i;
            int symJ = width-pos;
            if (isValidPosition(b, i, pos) && !((symI-i == 2 && pos == symJ && (i+1 < height && b.isWhiteCell(i+1, pos))) || (i == symI && symJ-pos == 2 && (pos+1 < width && b.isWhiteCell(i, pos+1))))) validPos.add(pos);
            else invalidPos.add(pos);
            allPos.add(pos);
            pos++;
        }
        if (size > 9) { //Maybe different values for difficulties??
            int iniPos = 1;
            int finPos = size < 9 ? size : 9;
            if (size/2 < 9) iniPos = size-9 > 0 ? size-9 : 0; // preferably do only one cut
            boolean foundCut = false;

            Collections.shuffle(validPos, random);
            // try to get just one cut
            for (int k = validPos.size()-1; !foundCut && k >= 0; k--) {
                int p = validPos.get(k);
                if (p < j+iniPos || p > j+finPos) continue;
                if (checkConnected(i, p, b)) {
                    b.setCell(new BlackCell(), i, p);
                    b.setCell(new BlackCell(), height-i, width-p);
                    foundCut = true;
                } else {
                    validPos.remove(k);
                }
            }
            // might get more than one cut
            for (int k = validPos.size()-1; !foundCut && k >= 0; k--) {
                int p = validPos.get(k);
                if (p > j+finPos) continue;
                if (checkConnected(i, p, b)) {
                    b.setCell(new BlackCell(), i, p);
                    b.setCell(new BlackCell(), height-i, width-p);
                    foundCut = true;
                }
            }
            // might get a row / column of length 1
            Collections.shuffle(invalidPos, random);
            for (int k = invalidPos.size()-1; !foundCut && k >= 0; k--) {
                int p = invalidPos.get(k);
                if (p > j+finPos) continue;
                if (checkConnected(i, p, b)) {
                    b.setCell(new BlackCell(), i, p);
                    b.setCell(new BlackCell(), height-i, width-p);
                    foundCut = true;
                }
            }
            // will get a disconnected board
            if (foundCut) {
                int randomChoice = random.nextInt(finPos);
                int p = j+1+randomChoice;
                b.setCell(new BlackCell(), i, p);
                b.setCell(new BlackCell(), height-i, width-p);
            }
        }
    }

    private void makeNecessaryPartitionCol(int i, int j, Board b) {
        int height = b.getHeight();
        int width = b.getWidth();
        int pos = i+1;
        int size = 0;
        ArrayList<Integer> validPos = new ArrayList<>();
        ArrayList<Integer> invalidPos = new ArrayList<>();
        while (pos < height && b.isWhiteCell(pos, j)) {
            size++;
            int symI = height-pos;
            int symJ = width-j;
            if (isValidPosition(b, pos, j) && !((symI-pos == 2 && j == symJ && (pos+1 < height && b.isWhiteCell(pos+1, j))) || (pos == symI && symJ-j == 2 && (j+1 < width && b.isWhiteCell(pos, j+1))))) validPos.add(pos);
            else invalidPos.add(pos);
            pos++;
        }
        if (size > 9) { //Maybe different values for difficulties??
            int iniPos = 1;
            int finPos = 9;
            if (size/2 < 9) iniPos = size - 9; // preferably do only one cut
            boolean foundCut = false;

            Collections.shuffle(validPos, random);
            // try to get just one cut
            for (int k = validPos.size()-1; !foundCut && k >= 0; k--) {
                int p = validPos.get(k);
                if (p < i+iniPos || p > i+finPos) continue;
                if (checkConnected(p, j, b)) {
                    b.setCell(new BlackCell(), p, j);
                    b.setCell(new BlackCell(), height-p, width-j);
                    foundCut = true;
                } else {
                    validPos.remove(k);
                }
            }
            // might get more than one cut
            for (int k = validPos.size()-1; !foundCut && k >= 0; k--) {
                int p = validPos.get(k);
                if (checkConnected(p, j, b)) {
                    b.setCell(new BlackCell(), p, j);
                    b.setCell(new BlackCell(), height-p, width-j);
                    foundCut = true;
                }
            }
            // might get a row / column of length 1
            Collections.shuffle(invalidPos, random);
            for (int k = invalidPos.size()-1; !foundCut && k >= 0; k--) {
                int p = invalidPos.get(k);
                if (checkConnected(p, j, b)) {
                    b.setCell(new BlackCell(), p, j);
                    b.setCell(new BlackCell(), height-p, width-j);
                    foundCut = true;
                }
            }
            // will get a disconnected board
            if (!foundCut) {
                int randomChoice = random.nextInt(size);
                int p = i+1+randomChoice;
                b.setCell(new BlackCell(), p, j);
                b.setCell(new BlackCell(), height-p, width-j);
            }
        }
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

        //once preprocesses are done we can initialize assigFunctions
        initializeAssigFunctions();

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
                        if (assigFunctions.cellValueAssignation(c.r, c.c, value)){
                            if (forceUniqueSolution) forcedValues.add(c);
                            finished = false;
                        }
                    }
                    if (workingBoard.isEmpty(c.r, c.c)) realAmbiguities.add(c);
                    if (!finished) break; // a value was assigned, should check the correct way of assigning values before continuing.
                }
            }
            if (!notationsQueue.isEmpty()) resolveEnqueuedCellValues(possibleAmbiguities);
        }
        //System.out.println("After ambiguity check");
        //printNotations();

        if (realAmbiguities.size() > 0) {
            //System.out.println("THIS SHOULDN'T HAPPEN!!! Cells are left without options... Unique solution can't be guaranteed");
            ArrayList<Coordinates> toSolve = new ArrayList<>();
            for (Coordinates c : realAmbiguities)
                if (workingBoard.isEmpty(c.r, c.c)) toSolve.add(c);
            provisionalFillInBacktracking(toSolve);
            computeRowSums();
            computeColSums();
        } else {
            //System.out.println("Unique solution found!");
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
            if (assigFunctions.bothRowColSumAssignationAssertCellValueAssigned(coordRow, coordCol, uniqueValue[0], uniqueValue[1])) return true;
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
                    if (workingBoard.isEmpty(coord.first, coord.second)) { // if valueBiasedSumAssignation is working perfectly this shouldn't happen
                        //System.out.println("Success at valueBiasedSumAssig but no value was assigned at iter: " + iter + ", coord: " + coord.first + "," + coord.second);
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
                        //System.out.println("Wasnt avoided but did success... shouldnt happen at iter: " + iter + ", coord: " + coord.first + "," + coord.second);
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
        fillWhiteCells(toSolve, 0); //System.out.println("No correct assignment of values found");
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
                        if (!assigFunctions.isCombinationPossible(p, containingCells)) possibilities.remove(k);
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
                    if (assigFunctions.rowSumAssignationAssertCellValueAssigned(r, c, rowSumCand)) break;
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
                        if (!assigFunctions.isCombinationPossible(p, containingCells)) possibilities.remove(k);
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
                    if (assigFunctions.colSumAssignationAssertCellValueAssigned(r, c, colSumCand)) break;
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
                    if (!assigFunctions.isCombinationPossible(p, containingCells)) rowCases.remove(i);
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
                    if (!assigFunctions.isCombinationPossible(p, containingCells)) colCases.remove(i);
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
                    if (assigFunctions.bothRowColSumAssignationAssertCellValueAssigned(r, c, uniqueComb.first, uniqueComb.second)) return true;
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

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Coordinates)) return false;
            return this.r == ((Coordinates)o).r && this.c == ((Coordinates)o).c;
        }
    }

    private void initializeAssigFunctions() {
        assigFunctions = new KakuroFunctions(new KakuroFunctions.KakuroFunctionsMaster() {
            @Override
            public int getRowID(int r, int c) {
                return rowLine[r][c];
            }

            @Override
            public int getColID(int r, int c) {
                return colLine[r][c];
            }

            @Override
            public int getRowLineSize(int r, int c) {
                return rowLineSize;
            }

            @Override
            public int getColLineSize(int r, int c) {
                return colLineSize;
            }

            @Override
            public int getRowSum(int r, int c) {
                return rowSums[rowLine[r][c]];
            }

            @Override
            public int getColSum(int r, int c) {
                return colSums[colLine[r][c]];
            }

            @Override
            public void setRowSum(int r, int c, int value) {
                if (value < 0 || value > 45) return;
                rowSums[rowLine[r][c]] = value;
            }

            @Override
            public void setColSum(int r, int c, int value) {
                if (value < 0 || value > 45) return;
                colSums[colLine[r][c]] = value;
            }

            @Override
            public int getRowSize(int r, int c) {
                return rowSize[rowLine[r][c]];
            }

            @Override
            public int getColSize(int r, int c) {
                return colSize[colLine[r][c]];
            }

            @Override
            public int getRowValuesUsed(int r, int c) {
                return rowValuesUsed[rowLine[r][c]];
            }

            @Override
            public int getColValuesUsed(int r, int c) {
                return colValuesUsed[colLine[r][c]];
            }

            @Override
            public void setRowValuesUsed(int r, int c, int values) {
                if (values>>9 > 0) return;
                rowValuesUsed[rowLine[r][c]] = values;
            }

            @Override
            public void setColValuesUsed(int r, int c, int values) {
                if (values>>9 > 0) return;
                colValuesUsed[colLine[r][c]] = values;
            }

            @Override
            public Pair<Integer, Integer> getFirstRowCoord(int r, int c) {
                return new Pair<>(firstRowCoord[rowLine[r][c]].r, firstRowCoord[rowLine[r][c]].c);
            }

            @Override
            public Pair<Integer, Integer> getFirstColCoord(int r, int c) {
                return new Pair<>(firstColCoord[colLine[r][c]].r, firstColCoord[colLine[r][c]].c);
            }

            @Override
            public Board getWorkingBoard() {
                return workingBoard;
            }

            @Override
            public SwappingCellQueue getNotationsQueue() {
                return notationsQueue;
            }
        });
    }
}
