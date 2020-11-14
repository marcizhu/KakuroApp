package src.controllers;

import src.domain.BlackCell;
import src.domain.Difficulty;
import src.domain.Board;
import src.domain.Cell;

public class EmptyBoardGenerator {
    private final Difficulty difficulty;
    private final Board board;

    public EmptyBoardGenerator(Difficulty difficulty, int width, int height) {
        this.difficulty = difficulty;
        board = new Board(width, height);
    }

    public void generateEmptyBoard() {
        /*
        Returns a board only containing empty black or white cells
         */
        int width = board.getWidth();
        int height = board.getHeight();

        for(int i = 0; i<height; i++) {
            for(int j = 0; j<width; j++) {
                Cell c;
                if (i == 0 || j == 0) {
                    c = new BlackCell();
                }
                else {

                }
            }
        }
    }
}

//getUniqueCrossValues(enum difficulty, int row_length, int col_length)