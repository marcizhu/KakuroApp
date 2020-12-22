package src.domain.algorithms;

import src.domain.algorithms.helpers.*;
import src.domain.entities.*;
import src.utils.IntPair;
import src.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Solver {
    private final Board initialBoard;
    private Board workingBoard;
    private final ArrayList<Board> solutions = new ArrayList<>();

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
     * @param board Board to solve
     */
    public Solver(Board board) {
        initialBoard = board;

        rows    = board.getHeight();
        columns = board.getWidth();

        rowLine = new int[board.getHeight()][board.getWidth()];
        colLine = new int[board.getHeight()][board.getWidth()];
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

        for (WhiteCell cell : forcedStartingValues) {
            int r = cell.getCoordinates().first;
            int c = cell.getCoordinates().second;
            if (!assigFunctions.cellValueAssignation(r, c, cell.getValue())) return 0;
        }

        for (int rowID = 0; rowID < rowLineSize; rowID++) {
            int r = firstRowCoord[rowID].first;
            int c = firstRowCoord[rowID].second-1;
            if (!assigFunctions.rowSumAssignation(r, c+1, initialBoard.getHorizontalSum(r, c))) return 0;
        }

        for (int colID = 0; colID < colLineSize; colID++) {
            int r = firstColCoord[colID].first-1;
            int c = firstColCoord[colID].second;
            if (!assigFunctions.colSumAssignation(r+1, c, initialBoard.getVerticalSum(r, c))) return 0;
        }

        if (notationsQueue.isEmpty()) {
            // there is only one solution,
            // we can't just add the working board because rowSum and colSum assignations aren't done directly to the black cells
            solutions.add(copySolution(workingBoard));
            return 1;
        }

        ArrayList<IntPair> remainingCells = new ArrayList<>();
        Board justInCaseBoard = new Board(workingBoard);
        int[] rowValuesUsedLocal = new int[rowLineSize];
        int[] colValuesUsedLocal = new int[colLineSize];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (workingBoard.isWhiteCell(r, c)) {
                    if (workingBoard.isEmpty(r, c)) {
                        remainingCells.add(new IntPair(r, c));
                        if (notationsQueue.isHiding(r, c)) notationsQueue.insertOrderedCell(r, c);
                    } else {
                        rowValuesUsedLocal[rowLine[r][c]] |= (1 << (workingBoard.getValue(r,c)-1));
                        colValuesUsedLocal[colLine[r][c]] |= (1 << (workingBoard.getValue(r,c)-1));
                    }
                }
            }
        }

        inferenceBacktracking();

        if (solutions.size() != 0) return solutions.size();

        Collections.sort(remainingCells, new Comparator<IntPair>() {
            @Override
            public int compare(IntPair a, IntPair b) {
                int aNot = justInCaseBoard.getCellNotationSize(a.first, a.second);
                int bNot = justInCaseBoard.getCellNotationSize(b.first, b.second);
                if (aNot < bNot) return -1;
                if (aNot > bNot) return 1;
                return a.compareTo(b);
            }
        });

        backtrackingSolve(justInCaseBoard, rowValuesUsedLocal, colValuesUsedLocal, remainingCells, 0);

        return solutions.size();
    }

    private Board copySolution(Board b) {
        Board solvedBoard = new Board(columns, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (b.isBlackCell(i, j)) {
                    solvedBoard.setCell(new BlackCell((BlackCell)initialBoard.getCell(i, j)), i, j);
                } else {
                    if (b.isEmpty(i, j)) {
                        solvedBoard.setCell(new WhiteCell(), i, j);
                    }
                    else solvedBoard.setCell(new WhiteCell(b.getValue(i, j)), i, j);
                }
            }
        }
        return solvedBoard;
    }

    private void inferenceBacktracking() {
        if (solutions.size() > 1) return;
        if (notationsQueue.isEmpty()) {
            solutions.add(copySolution(workingBoard));
            return;
        }

        ArrayList<Pair<Integer, Integer>> rowSumsRollback = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> colSumsRollback = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> cellValuesRollback = new ArrayList<>();
        ArrayList<Pair<Pair<Integer, Integer>, Integer>> cellNotationsRollback = new ArrayList<>();

        assigFunctions.setAssignationEventListener(new KakuroFunctions.AssignationEventListener() {
            @Override
            public void onCellValueAssignation(Pair<Pair<Integer, Integer>, Integer> p) {
                cellValuesRollback.add(p.first);
            }
            @Override
            public void onCellNotationsChanged(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> p) {
                cellNotationsRollback.add(new Pair<>(p.first, p.second.first));
            }
            @Override
            public void onRowSumAssignation(Pair<Pair<Integer, Integer>, Integer> p) {
                rowSumsRollback.add(p.first);
            }
            @Override
            public void onColSumAssignation(Pair<Pair<Integer, Integer>, Integer> p) {
                colSumsRollback.add(p.first);
            }
            @Override
            public void onCellNoValuesLeft(Pair<Integer, Integer> coord) {}
            @Override
            public void onRowNoValuesLeft(Pair<Integer, Integer> coord) {}
            @Override
            public void onColNoValuesLeft(Pair<Integer, Integer> coord) {}
        });


        WhiteCell cell = notationsQueue.getFirstElement();
        int notations = cell.getNotations();

        for (int i = 0; i < 9; i++) {
            if ((notations&(1<<i)) == 0) continue;
            if (assigFunctions.cellValueAssignation(cell.getCoordinates().first, cell.getCoordinates().second, i+1)) {
                inferenceBacktracking();
                if (solutions.size() > 1) return;
                assigFunctions.externalRollback(
                        rowSumsRollback,
                        colSumsRollback,
                        cellValuesRollback,
                        cellNotationsRollback,
                        new ArrayList<>()
                );
            }
            rowSumsRollback.clear();
            colSumsRollback.clear();
            cellValuesRollback.clear();
            cellNotationsRollback.clear();
            if (solutions.size() > 1) return;
        }
    }

    private int getPossibleValues(int[] rowValuesUsedLocal, int[] colValuesUsedLocal, int row, int col) {
        // Get options for each row and column
        final int rowID = rowLine[row][col];
        final int colID = colLine[row][col];
        int hAvailable = 0;
        int vAvailable = 0;

        ArrayList<Integer> hOptions =
                KakuroConstants.INSTANCE.getPossibleCasesWithValues(rowSize[rowID], rowSums[rowID], rowValuesUsedLocal[rowID]);
        ArrayList<Integer> vOptions =
                KakuroConstants.INSTANCE.getPossibleCasesWithValues(colSize[colID], colSums[colID], colValuesUsedLocal[colID]);

        // Calculate available options for this row
        for (Integer i : hOptions)
            hAvailable |= i;

        // Calculate available options for this column
        for (Integer i : vOptions)
            vAvailable |= i;

        // Do the intersection
        return hAvailable & vAvailable   // A value is available if it is available in both row & col...
                & ~colValuesUsedLocal[colID]  // ...and it is not used in the current column...
                & ~rowValuesUsedLocal[rowID]; // ...nor in the current row.
    }

    private void backtrackingSolve(Board b, int[] rowValuesUsedLocal, int[] colValuesUsedLocal, ArrayList<IntPair> cells, int idx) {
        // Check if we found a solution
        if (cells.size() == idx) {
            // At this point a solution has been found
            // Add a copy of this board to the list of solutions
            solutions.add(copySolution(b));
            return;
        }

        int row = cells.get(idx).first;
        int col = cells.get(idx).second;

        int possibleValues = getPossibleValues(rowValuesUsedLocal, colValuesUsedLocal, row, col);

        for (int i = 1; i <= 9; i++) {
            if(((possibleValues >> (i - 1)) & 1) == 0) continue; // if n-th bit is 0, skip

            b.setCellValue(row, col, i);
            rowValuesUsedLocal[rowLine[row][col]] |= (1 << (i-1));
            colValuesUsedLocal[colLine[row][col]] |= (1 << (i-1));
            backtrackingSolve(b, rowValuesUsedLocal, colValuesUsedLocal, cells, idx+1);
            b.clearCellValue(row, col);
            rowValuesUsedLocal[rowLine[row][col]] &= ~(1 << (i-1));
            colValuesUsedLocal[colLine[row][col]] &= ~(1 << (i-1));
            if (solutions.size() > 1) return;
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
