package src.controllers;

import src.domain.BlackCell;
import src.domain.Cell;
import src.domain.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Solver {
	private Board board;
	private ArrayList<Board> solutions;
	private int[][] rowSums;
    private int[][] colSums;
    private int[][] rowSize;
    private int[][] colSize;

	public Solver(Board b) {
		this.board = b;
        this.solutions = new ArrayList<>();
        this.rowSums = new int[board.getHeight()][board.getWidth()];
        this.colSums = new int[board.getHeight()][board.getWidth()];
        this.rowSize = new int[board.getHeight()][board.getWidth()];
        this.colSize = new int[board.getHeight()][board.getWidth()];
    }

    public void solve() {
	    preprocessRowSums();
	    preprocessColSums();
	    preprocessRowSize();
        preprocessColSize();

        solve(0, 0, 0);
    }

    private void preprocessRowSums() {
	    // Calculate sums for each row
	    for(int i = 0; i < board.getHeight(); i++) {
	        for(int j = 0; j < board.getWidth(); j++) {
	            if(board.isBlackCell(i, j)) {
	                rowSums[i][j] = ((BlackCell)board.getCell(i, j)).getHorizontalSum();
                } else {
	                // TODO: Check for out-of-bounds access.
                    rowSums[i][j] = rowSums[i][j - 1];
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

    private void preprocessColSums() {
        for(int i = 0; i < board.getHeight(); i++) {
            for(int j = 0; j < board.getWidth(); j++) {
                if(board.isBlackCell(i, j)) {
                    colSums[i][j] = ((BlackCell)board.getCell(i, j)).getVerticalSum();
                } else {
                    // TODO: Check for out-of-bounds access.
                    colSums[i][j] = colSums[i - 1][j];
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

    // TODO: reimplement using KakuroConstants to get possible combinations
    private ArrayList<Integer> getPossibleValues(int row, int col, int rowSum) {
        ArrayList<Integer> possibleValues = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        int horizontalSum = rowSums[row][col];
        int horizontalCurrentSum = rowSum;

        // TODO: Should be fixed but it's not tested
        int finalHorizontalCurrentSum = horizontalCurrentSum;
        possibleValues.removeIf(n -> ((n + finalHorizontalCurrentSum) > horizontalSum));

        int verticalSum = colSums[row][col];
        int verticalCurrentSum = 0;
        int verticalEmptyCount = 1;
        for (int r = row - 1; r > 0; r--) {
            Cell cell = board.getCell(r, col);
            if (cell instanceof BlackCell) {
                break;
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    verticalCurrentSum += value;
                    possibleValues.removeIf(val -> val == value);
                }
                else verticalEmptyCount++;
            }
        }
        for (int r = row + 1; r < board.getHeight(); r++) {
            Cell cell = board.getCell(r, col);
            if (cell instanceof BlackCell) {
                break;
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    verticalCurrentSum += value;
                    possibleValues.removeIf(val -> val == value);
                }
                else verticalEmptyCount++;
            }
        }

        int finalVerticalCurrentSum = verticalCurrentSum;
        possibleValues.removeIf(n -> ((n + finalVerticalCurrentSum) > verticalSum));

        return possibleValues;
    }

    private void solve(int row, int col, int rowSum) {
        if (row == board.getHeight() - 1 && col == board.getWidth()) {
            // At this point a solution has been found
            // Add a copy of this board to the list of solutions
            solutions.add(new Board(board));
            return;
        }

        if (col >= board.getWidth()) {
            solve(row + 1, 0, 0);
            return;
        }

        if (board.isBlackCell(row, col)) {
            solve(row, col + 1, 0); // If cell type is black, continue solving
            return;
        }

        ArrayList<Integer> possibleValues = getPossibleValues(row, col, rowSum);

        for (int i : possibleValues) {
            board.setCellValue(row, col, i);
            solve(row, col + 1, rowSum + i);
            board.clearCellValue(row, col);
            if (solutions.size() > 1) return;
        }
    }

	public ArrayList<Board> getSolutions() {
		return solutions;
    }
}
