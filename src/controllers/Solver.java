package src.controllers;

import src.domain.Board;

import java.util.ArrayList;

public class Solver {
    private final Board board;
    private final ArrayList<Board> solutions;

    private final int[][] rowLine;
    private final int[][] rowSums; // TODO: migrate this so that it uses rowLine
    private final int[][] rowSize; // TODO: migrate this so that it uses rowLine
    private boolean[][] rowValuesUsed;

    private final int[][] colLine;
    private final int[][] colSums; // TODO: migrate this so that it uses rowLine
    private final int[][] colSize; // TODO: migrate this so that it uses rowLine
    private boolean[][] colValuesUsed;

    public Solver(Board b) {
        board = b;
        solutions = new ArrayList<>();

        rowLine = new int[board.getHeight()][board.getWidth()];
        rowSums = new int[board.getHeight()][board.getWidth()];
        rowSize = new int[board.getHeight()][board.getWidth()];

        colLine = new int[board.getHeight()][board.getWidth()];
        colSums = new int[board.getHeight()][board.getWidth()];
        colSize = new int[board.getHeight()][board.getWidth()];
    }

    public void solve() {
        preprocessRowValuesUsed();
        preprocessRowSize();

        preprocessColValuesUsed();
        preprocessColSize();

        preprocessSums();

        solve(0, 0, 0, new int[board.getWidth()]);
    }

    private void preprocessRowValuesUsed() {
        int rowLineID = 0;
        for(int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                if(board.isBlackCell(i, j)) {
                    rowLine[i][j] = -1; // black cell is not responsible for any rowLine
                    if (j-1 >= 0 && board.isWhiteCell(i, j-1)) { // there is a row before the black cell
                        rowLineID++; // prepare for next rowLine
                    }
                } else {
                    // assign rowLineID to this member of current rowLine
                    rowLine[i][j] = rowLineID;
                }
            }
            if (board.isWhiteCell(i, board.getWidth() - 1)) { // last rowLine in row if we have seen whiteCells
                rowLineID++; // prepare for next rowLine
            }
        }
        rowValuesUsed = new boolean[rowLineID][9];
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                if (board.isWhiteCell(i, j)) {
                    int value = board.getValue(i, j);
                    if (value != 0) rowValuesUsed[rowLine[j][i]][value-1] = true;
                }
            }
        }
    }

    private void preprocessRowSize() {
        int size = 0;
        for(int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                if(board.isBlackCell(i, j)) {
                    for(int k = size; k > 0; k--)
                        rowSize[i][j - k] = size;
                    size = 0;
                } else {
                    size++;
                }
            }

            for(int k = size; k > 0; k--)
                rowSize[i][board.getWidth() - k] = size;
            size = 0;
        }
    }

    private void preprocessColValuesUsed() {
        int colLineID = 0;
        for(int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if(board.isBlackCell(j, i)) {
                    colLine[j][i] = -1; // black cell is not responsible for any colLine
                    if (j-1 >= 0 && board.isWhiteCell(j-1, i)) { // there is a col before the black cell
                        colLineID++; // prepare for next colLine
                    }
                } else {
                    // assign colLineID to this member of current colLine
                    colLine[j][i] = colLineID;
                }
            }
            if (board.isWhiteCell(board.getHeight() - 1, i)) { // last colLine in col if we have seen whiteCells
                colLineID++; // prepare for next colLine
            }
        }
        colValuesUsed = new boolean[colLineID][9];
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.isWhiteCell(j, i)) {
                    int value = board.getValue(j, i);
                    if (value != 0) colValuesUsed[colLine[j][i]][value-1] = true;
                }
            }
        }
    }

    private void preprocessColSize() {
        int size = 0;
        for (int j = 0; j < board.getWidth(); j++) {
            for(int i = 0; i < board.getHeight(); i++) {
                if(board.isBlackCell(i, j)) {
                    for(int k = size; k > 0; k--)
                        colSize[i - k][j] = size;
                    size = 0;
                } else {
                    size++;
                }
            }

            for(int k = size; k > 0; k--)
                colSize[board.getHeight() - k][j] = size;
            size = 0;
        }
    }

    private void preprocessSums() {
        // Calculate sums for each row & column
        for(int i = 0; i < board.getHeight(); i++) {
            for(int j = 0; j < board.getWidth(); j++) {
                if(board.isBlackCell(i, j)) {
                    rowSums[i][j] = board.getHorizontalSum(i, j);
                    colSums[i][j] = board.getVerticalSum(i, j);
                } else {
                    // TODO: Check for out-of-bounds access.
                    rowSums[i][j] = rowSums[i][j - 1];
                    colSums[i][j] = colSums[i - 1][j];
                }
            }
        }
    }

    private boolean[] getPossibleValues(int row, int col) {
        // Get options for each row and column
        ArrayList<ArrayList<Integer>> hOptions = KakuroConstants.INSTANCE.getPossibleCasesWithValues(
                rowSize[row][col], rowSums[row][col], rowValuesUsed[rowLine[row][col]]);
        ArrayList<ArrayList<Integer>> vOptions = KakuroConstants.INSTANCE.getPossibleCasesWithValues(
                colSize[row][col], colSums[row][col], colValuesUsed[colLine[row][col]]);

        boolean[] hAvailable = { false, false, false, false, false, false, false, false, false };
        boolean[] vAvailable = { false, false, false, false, false, false, false, false, false };

        // Calculate available options for this row
        for (ArrayList<Integer> hOpt : hOptions)
            for (Integer i : hOpt)
                hAvailable[i - 1] = true;

        // Calculate available options for this column
        for (ArrayList<Integer> vOpt : vOptions)
            for (Integer i : vOpt)
                vAvailable[i - 1] = true;

        // Do the intersection
        boolean[] availableValues = { false, false, false, false, false, false, false, false, false };

        for(int i = 0; i < 9; i++) {
            availableValues[i] =
                    hAvailable[i] && vAvailable[i] // A value is available if it is available in both row & col...
                            && !colValuesUsed[colLine[row][col]][i]  // ...and it is not used in the current column...
                            && !rowValuesUsed[rowLine[row][col]][i]; // ...nor in the current row.
        }

        return availableValues;
    }

    private void solve(int row, int col, int rowSum, int[] colSum) {
        // Check if we found a solution
        if (row == board.getHeight() - 1 && col == board.getWidth()) {
            if (rowSum != rowSums[row][col - 1]) return;
            for (int i = 0; i < board.getWidth(); i++)
                if (colSum[i] != colSums[row][i]) return;

            // At this point a solution has been found
            // Add a copy of this board to the list of solutions
            solutions.add(new Board(board));
            return;
        }

        if (col >= board.getWidth()) {
            if(rowSum != rowSums[row][col - 1]) return; // if row sum is not correct, return

            solve(row + 1, 0, 0, colSum);
            return;
        }

        if (board.isBlackCell(row, col)) {
            // If cell type is black, continue solving
            if (col > 0 && rowSum != rowSums[row][col - 1]) return; // if row sum is not correct, return
            if (row > 0 && colSum[col] != colSums[row - 1][col]) return; // if col sum is not correct, return

            int colSumTemp = colSum[col];
            colSum[col] = 0;
            solve(row, col + 1, 0, colSum);
            colSum[col] = colSumTemp;
            return;
        }

        if (!board.isEmpty(row, col)) {
            // If cell has a value, continue solving
            int val = board.getValue(row, col);
            colSum[col] += val;
            solve(row, col + 1, rowSum + val, colSum);
            colSum[col] -= val;
            return;
        }

        boolean[] possibleValues = getPossibleValues(row, col);

        for (int i = 1; i <= 9; i++) {
            if (!possibleValues[i - 1]) continue;

            board.setCellValue(row, col, i);
            rowValuesUsed[rowLine[row][col]][i-1] = true;
            colValuesUsed[colLine[row][col]][i-1] = true;
            colSum[col] += i;
            solve(row, col + 1, rowSum + i, colSum);
            board.clearCellValue(row, col);
            rowValuesUsed[rowLine[row][col]][i-1] = false;
            colValuesUsed[colLine[row][col]][i-1] = false;
            colSum[col] -= i;
            if (solutions.size() > 1) return;
        }
    }

    public ArrayList<Board> getSolutions() {
        return solutions;
    }
}
