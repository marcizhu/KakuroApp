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
    private Board workingBoard;

    private final int rows, columns;

    public CreatorCtrl(User user, int numRows, int numColumns) {
        this.user = user;
        workingBoard = new Board(numRows, numColumns);
        rows = numRows;
        columns = numColumns;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (r == 0 || c == 0) workingBoard.setCell(new BlackCell(), r, c);
                else workingBoard.setCell(new WhiteCell(true), r, c);
            }
        }
    }

    public void setUp(CreatorScreenCtrl view) {
        this.viewCtrl = view;
    }

    public String getBoardToString() {
        int height = workingBoard.getHeight();
        int width = workingBoard.getWidth();
        String[] row = new String[height];
        String[] col = new String[width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (workingBoard.isBlackCell(i, j)) {
                    if (workingBoard.getHorizontalSum(i, j) == 0 && workingBoard.getVerticalSum(i,j) == 0) {
                        int sections = 0;
                        if (i-1>=0 && workingBoard.isWhiteCell(i-1, j)) sections |= (1<<(viewCtrl.BLACK_SECTION_TOP));
                        if (i+1<height && workingBoard.isWhiteCell(i+1, j)) sections |= (1<<(viewCtrl.BLACK_SECTION_BOTTOM));
                        if (j-1>=0 && workingBoard.isWhiteCell(i, j-1)) sections |= (1<<(viewCtrl.BLACK_SECTION_LEFT));
                        if (j+1<width && workingBoard.isWhiteCell(i, j+1)) sections |= (1<<(viewCtrl.BLACK_SECTION_RIGHT));
                        col[j] = "#"+sections;
                    } else {
                        col[j] = workingBoard.getCell(i,j).toString();
                    }
                } else {
                    col[j] = workingBoard.getCell(i,j).toString();
                }
            }

            row[i] = String.join(",", col);
        }

        String header = height + "," + width + "\n";
        return header + String.join("\n", row);
    }
}
