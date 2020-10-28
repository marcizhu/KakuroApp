package src.controllers;

import jdk.jshell.spi.ExecutionControl;
import src.domain.BlackCell;
import src.domain.Board;
import src.domain.Cell;

import java.util.ArrayList;

public class Solver {
	Board board;
	int solutions;

	public Solver(Board b) {
		this.board = b;
        this.solutions = -1;
    }

    public Solver(String formattedBoard) {
		this.board = new Board(formattedBoard);
        this.solutions = -1;
    }

    public void solve() {
        solutions = 0;
        solve(0, 0);
    }

    // TODO: Remove exception signature once this function is implemented
    private ArrayList<Integer> getPossibleValues(int row, int col) throws ExecutionControl.NotImplementedException {
	    throw new ExecutionControl.NotImplementedException("TODO");
    }

    private void solve(int row, int col) {
        if (row == board.getHeight() - 1 && col == board.getWidth()) {
            // At this point a solution has been found
            return;
        }

        if(col >= board.getWidth()) {
            solve(row + 1, 0);
            return;
        }

        if(board.getCell(row, col) instanceof BlackCell) {
            // If cell type is black, continue solving
            solve(row, col+1);
            return;
        }

        ArrayList<Integer> possibleValues;
        try {
            possibleValues = getPossibleValues(row, col);
        } catch (Exception e) {
            return;
        }

        for (int i : possibleValues) {
            board.setCellValue(row, col, i);
            solve(row, col+1);
            board.setCellValue(row, col, 0); // Setting it to 0 counts as empty
            if (solutions > 1) return;
        }
        
    }

	public Board retrieveResult() {
		return board;
    }
}
