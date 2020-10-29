package src.controllers;

import src.domain.BlackCell;
import src.domain.Cell;
import src.domain.Board;

import java.util.ArrayList;
import java.util.Arrays;

public class Solver {
	Board board;

	ArrayList<Board> solutions;

	public Solver(Board b) {
		this.board = b;
        this.solutions = new ArrayList<>();
    }

    public void solve() {
        solve(0, 0);
    }

    // FIXME: Tested, but throws ConcurrentModificationException on line 64. If this function is fixed, the solver
    //        should work
    private ArrayList<Integer> getPossibleValues(int row, int col) {
        ArrayList<Integer> possibleValues = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        int horizontalSum = 45;
        int horizontalCurrentSum = 0;
        int horizontalEmptyCount = 1;
        for (int c = col - 1; c > 0; c--) {
            Cell cell = board.getCell(row, c);
            if (cell instanceof BlackCell) {
                int sum = ((BlackCell) cell).getHorizontalSum();
                if (sum != 0) {
                    horizontalSum = sum;
                    break;
                }
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    horizontalCurrentSum += value;
                    possibleValues.remove(value);
                }
                else horizontalEmptyCount++;
            }
        }
        for (int c = col + 1; c < board.getWidth(); c++) {
            Cell cell = board.getCell(row, c);
            if (cell instanceof BlackCell) {
                int sum = ((BlackCell) cell).getHorizontalSum();
                if (sum != 0) break;
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    horizontalCurrentSum += value;
                    possibleValues.remove(value);
                }
                else horizontalEmptyCount++;
            }
        }
        for (int value : possibleValues) // TODO: use horizontalEmptyCount to make this part smarter
            if (value + horizontalCurrentSum > horizontalSum)
                possibleValues.remove(value);

        int verticalSum = 45;
        int verticalCurrentSum = 0;
        int verticalEmptyCount = 1;
        for (int r = row - 1; r > 0; r--) {
            Cell cell = board.getCell(r, col);
            if (cell instanceof BlackCell) {
                int sum = ((BlackCell) cell).getVerticalSum();
                if (sum != 0) {
                    verticalSum = sum;
                    break;
                }
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    verticalCurrentSum += value;
                    possibleValues.remove(value);
                }
                else verticalEmptyCount++;
            }
        }
        for (int r = row + 1; r < board.getHeight(); r++) {
            Cell cell = board.getCell(r, col);
            if (cell instanceof BlackCell) {
                int sum = ((BlackCell) cell).getHorizontalSum();
                if (sum != 0) break;
            } else { // cell is an instance of WhiteCell
                if (!cell.isEmpty()) {
                    int value = cell.getValue();
                    verticalCurrentSum += value;
                    possibleValues.remove(value);
                }
                else verticalEmptyCount++;
            }
        }
        for (int value : possibleValues) // TODO: use verticalEmptyCount to make this part smarter
            if (value + verticalCurrentSum > verticalSum)
                possibleValues.remove(value);

        return possibleValues;
    }

    private void solve(int row, int col) {
        if (row == board.getHeight() - 1 && col == board.getWidth()) {
            // At this point a solution has been found
            // Add a copy of this board to the list of solutions
            solutions.add(new Board(board));
            return;
        }

        if (col >= board.getWidth()) {
            solve(row + 1, 0);
            return;
        }

        if (board.isBlackCell(row, col)) {
            solve(row, col + 1); // If cell type is black, continue solving
            return;
        }

        ArrayList<Integer> possibleValues = getPossibleValues(row, col);

        for (int i : possibleValues) {
            board.setCellValue(row, col, i);
            solve(row, col + 1);
            board.clearCellValue(row, col);
            if (solutions.size() > 1) return;
        }
    }

	public ArrayList<Board> getSolutions() {
		return solutions;
    }
}
