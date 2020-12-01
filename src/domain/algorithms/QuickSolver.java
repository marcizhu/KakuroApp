package src.domain.algorithms;

import src.domain.algorithms.helpers.KakuroFunctions;
import src.domain.algorithms.helpers.SwappingCellQueue;
import src.domain.entities.BlackCell;
import src.domain.entities.Board;
import src.domain.entities.WhiteCell;
import src.utils.Pair;

import java.util.ArrayList;

public class QuickSolver {
    private final Board initialBoard;
    private Board workingBoard;
    private final ArrayList<Board> solutions;

    private SwappingCellQueue notationsQueue;
    private KakuroFunctions assigFunctions;

    private final int rows, columns;

    private int[] rowSums;                  // value of sums assigned to rowLines, 0 means not assigned
    private int[] rowSize;                  // sizes of each rowLine, always between 0 and 9
    private int[] rowValuesUsed;            // cell values used (assigned) in each rowLine, always boolean[9]
    private Pair<Integer, Integer>[] firstRowCoord;    // coordinates to the first white cell in each rowLine
    private final int[][] rowLine;          // Pointers to position at arrays of row related data
    private int rowLineSize;                // Number of different rowLines

    private int[] colSums;                  // value of sums assigned to colLines, 0 means not assigned
    private int[] colSize;                  // sizes of each colLine, always between 0 and 9
    private int[] colValuesUsed;            // cell values used (assigned) in each colLine, always boolean[9]
    private Pair<Integer, Integer>[] firstColCoord;    // coordinates to the first white cell in each colLine
    private final int[][] colLine;          // Pointers to position at array of colValuesUsed and colSums
    private int colLineSize;                // Number of different colLines

    /**
     * Constructor.
     * Initializes the solver to solve the given board
     * @param b Board to solve
     */
    public QuickSolver(Board b) {
        initialBoard = b;
        solutions = new ArrayList<>();
        rows = b.getHeight();
        columns = b.getWidth();
        rowLine = new int[b.getHeight()][b.getWidth()];
        colLine = new int[b.getHeight()][b.getWidth()];
    }

    /**
     * Get solutions of the board.
     * This function *MUST* be called after a call to `solve()`
     * @return an ArrayList with all possible solutions of the board
     */
    public ArrayList<Board> getSolutions() {
        return solutions;
    }

    private void preprocessRows() {
        int size = 0;
        int rowLineID = 0;
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> coord = new ArrayList<>();
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(initialBoard.isBlackCell(i, j)) {
                    if (j-1 >= 0 && initialBoard.isWhiteCell(i, j-1)) {// there is a row before the black cell
                        sizes.add(size);
                        size = 0;
                        rowLineID++; //prepare for next rowLine
                    }
                    if (j+1 < columns && initialBoard.isWhiteCell(i, j+1)) {
                        rowLine[i][j] = rowLineID; //black cell is responsible for next rowLine
                        coord.add(new Pair<>(i, j+1));
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
        firstRowCoord = new Pair[rowLineSize];
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
        ArrayList<Pair<Integer, Integer>> coord = new ArrayList<>();
        for(int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                if(initialBoard.isBlackCell(j, i)) {
                    if (j-1 >= 0 && initialBoard.isWhiteCell(j-1, i)) {// there is a col before the black cell
                        sizes.add(size);
                        size = 0;
                        colLineID++; //prepare for next colLine
                    }
                    if (j+1 < rows && initialBoard.isWhiteCell(j+1, i)) {
                        colLine[j][i] = colLineID; //black cell is responsible for next colLine
                        coord.add(new Pair<>(j+1, i));
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
        firstColCoord = new Pair[colLineSize];
        for (int i = 0; i < colLineSize; i++) { //initialize data at default values
            colSums[i] = 0;
            colSize[i] = sizes.get(i);
            colValuesUsed[i] = 0;
            firstColCoord[i] = coord.get(i);
        }
    }

    /**
     * Solve the board
     * @return the number of solutions of the board
     */
    public int solve() {
        preprocessRows();
        preprocessCols();

        // Prepare working board:
        workingBoard = new Board(columns, rows);
        ArrayList<WhiteCell> forcedStartingValues = new ArrayList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (initialBoard.isBlackCell(r, c)) {
                    workingBoard.setCell(new BlackCell(), r, c);
                } else {
                    if (!initialBoard.isEmpty(r, c)) forcedStartingValues.add((WhiteCell)initialBoard.getCell(r, c));
                    workingBoard.setCell(new WhiteCell(true), r, c);
                }
            }
        }
        notationsQueue = new SwappingCellQueue(workingBoard);
        initializeAssigFunctions();

        boolean zeroSolutions = false;

        for (WhiteCell cell : forcedStartingValues) {
            int r = cell.getCoordinates().first;
            int c = cell.getCoordinates().second;
            if (!assigFunctions.cellValueAssignation(r, c, cell.getValue())) {
                zeroSolutions = true;
                break;
            }
        }

        for (int rowID = 0; !zeroSolutions && rowID < rowLineSize; rowID++) {
            int r = firstRowCoord[rowID].first;
            int c = firstRowCoord[rowID].second-1;
            if (!assigFunctions.rowSumAssignation(r, c+1, initialBoard.getHorizontalSum(r, c))) zeroSolutions = true;
        }

        for (int colID = 0; !zeroSolutions && colID < colLineSize; colID++) {
            int r = firstColCoord[colID].first-1;
            int c = firstColCoord[colID].second;
            if (!assigFunctions.colSumAssignation(r+1, c, initialBoard.getVerticalSum(r, c))) zeroSolutions = true;
        }

        if (zeroSolutions) return 0;

        //FIXME: this is just to test the old solver capabilities fo solving the rest of the board, should go only inside the next if statement
        Board solvedBoard = new Board(columns, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (workingBoard.isBlackCell(i, j)) {
                    solvedBoard.setCell(new BlackCell((BlackCell)initialBoard.getCell(i, j)), i, j);
                } else if (!workingBoard.isEmpty(i, j)) {
                    solvedBoard.setCell(new WhiteCell(workingBoard.getValue(i, j)), i, j);
                } else {
                    solvedBoard.setCell(new WhiteCell(), i, j);
                }
            }
        }

        if (notationsQueue.isEmpty()) {
            // there is only one solution,
            // we can't just add the working board because rowSum and colSum assignations aren't done directly to the black cells
            /*Board solvedBoard = new Board(columns, rows);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (workingBoard.isBlackCell(i, j)) {
                        solvedBoard.setCell(new BlackCell((BlackCell)initialBoard.getCell(i, j)), i, j);
                    } else {
                        solvedBoard.setCell(new WhiteCell(workingBoard.getValue(i, j)), i, j);
                    }
                }
            }*/
            solutions.add(solvedBoard);
            return 1;
        }

        //System.out.println("Board has multiple solutions, to be implemented using old solver. Remaining cells to be assigned: " + (notationsQueue.endElement - notationsQueue.firstElement));

        Solver s = new Solver(solvedBoard);
        s.solve();
        solutions.addAll(s.getSolutions());
        return 2;
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
                return firstRowCoord[rowLine[r][c]];
            }

            @Override
            public Pair<Integer, Integer> getFirstColCoord(int r, int c) {
                return firstColCoord[colLine[r][c]];
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
