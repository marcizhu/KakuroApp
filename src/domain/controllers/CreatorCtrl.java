package src.domain.controllers;

import src.domain.entities.BlackCell;
import src.domain.entities.Board;
import src.domain.entities.User;
import src.domain.entities.WhiteCell;
import src.presentation.controllers.CreatorScreenCtrl;
import src.presentation.controllers.GameScreenCtrl;

public class CreatorCtrl {

    private CreatorScreenCtrl viewCtrl;

    private User user;
    private Board initialBoard;

    private final int rows, columns;

    public CreatorCtrl(User user, int numRows, int numColumns) {
        this.user = user;
        initialBoard = new Board(numRows, numColumns);
        rows = numRows;
        columns = numColumns;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (r == 0 || c == 0) initialBoard.setCell(new BlackCell(), r, c);
                else initialBoard.setCell(new WhiteCell(true), r, c);
            }
        }
    }

    public void setUp(CreatorScreenCtrl view) {
        this.viewCtrl = view;
    }

    public String getBoardToString() {
        return initialBoard.toString();
    }
}
