package src.controllers;

import src.domain.Board;

import java.util.ArrayList;

public class Solver {
    private final Board board;
    private final ArrayList<Board> solutions;
    private final int[][] rowSums; ///< The total sum of a row for a cell
    private final int[][] colSums; ///< The total sum of a column for a cell
    private final int[][] rowSize; ///< The size of the row for a cell
    private final int[][] colSize; ///< The size of the column for a cell

    public Solver(Board b) {
        board = b;
        solutions = new ArrayList<>();
        rowSums = new int[board.getHeight()][board.getWidth()];
        colSums = new int[board.getHeight()][board.getWidth()];
        rowSize = new int[board.getHeight()][board.getWidth()];
        colSize = new int[board.getHeight()][board.getWidth()];
    }

    public void solve() {
        preprocessSums();
        preprocessRowSize();
        preprocessColSize();

        solve(0, 0, 0, new int[board.getWidth()]);
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

    // TODO: This could be optimized even further: this class could hold an NxN matrix of bools representing
    //       which values are used in each row & column. That would remove the need for the 4 first for loops
    //       in this function, greatly reducing the execution time
    private boolean[] getPossibleValues(int row, int col) {
        // Horizontal
        boolean[] hUsedValues = { false, false, false, false, false, false, false, false, false };

        for(int it = col - 1; board.isWhiteCell(row, it) && it >= 0; it--) {
            int v = board.getValue(row, it);
            if (v != 0) hUsedValues[v - 1] = true;
        }

        for(int it = col + 1; it < board.getWidth() && board.isWhiteCell(row, it); it++) {
            int v = board.getValue(row, it);
            if (v != 0) hUsedValues[v - 1] = true;
        }

        // Vertical
        boolean[] vUsedValues = { false, false, false, false, false, false, false, false, false };

        for(int it = row - 1; board.isWhiteCell(it, col) && it >= 0; it--) {
            int v = board.getValue(it, col);
            if (v != 0) vUsedValues[v - 1] = true;
        }

        for(int it = row + 1; it < board.getHeight() && board.isWhiteCell(it, col); it++) {
            int v = board.getValue(it, col);
            if (v != 0) vUsedValues[v - 1] = true;
        }

        // Get options for each row and column
        ArrayList<ArrayList<Integer>> hOptions = KakuroConstants.INSTANCE.getPossibleCasesWithValues(rowSize[row][col], rowSums[row][col], hUsedValues);
        ArrayList<ArrayList<Integer>> vOptions = KakuroConstants.INSTANCE.getPossibleCasesWithValues(colSize[row][col], colSums[row][col], vUsedValues);

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
                    && !vUsedValues[i]  // ...and it is not used in the current column...
                    && !hUsedValues[i]; // ...nor in the current row.
        }

        return availableValues;
    }

    private void solve(int row, int col, int rowSum, int[] colSum) {
        if (row == board.getHeight() - 1 && col == board.getWidth()) {
            if (rowSum != rowSums[row][col - 1]) return;

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
            colSum[col] += i;
            solve(row, col + 1, rowSum + i, colSum);
            colSum[col] -= i;
            board.clearCellValue(row, col);
            if (solutions.size() > 1) return;
        }
    }

    public ArrayList<Board> getSolutions() {
        return solutions;
    }
}
