package src.controllers;

import src.domain.BlackCell;
import src.domain.Board;
import src.domain.Cell;
import src.domain.WhiteCell;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Writer {
    private String fileName;

    public Writer(String fileName) {
        this.fileName = fileName;
    }

    public void write(Board board) {
        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.print(board.getHeight());
            writer.print(',');
            writer.println(board.getWidth());

            for (int i = 0; i < board.getHeight(); i++) {
                for (int j = 0; j < board.getWidth(); j++) {
                    Cell cell = board.getCell(i, j);

                    if (cell instanceof BlackCell) {
                        int col = ((BlackCell) cell).getVerticalSum();
                        int row = ((BlackCell) cell).getHorizontalSum();

                        if (col == 0 && row == 0) {
                            writer.print('*');
                        }
                        else {
                            if (col > 0) {
                                writer.print('C');
                                writer.print(col);
                            }
                            if (row > 0) {
                                writer.print('F');
                                writer.print(row);
                            }
                        }
                    } else if (cell instanceof WhiteCell) {
                        int value = cell.getValue();
                        writer.write(value == 0 ? "?" : Integer.toString(value));
                    }

                    if(j + 1 < board.getWidth()) writer.print(',');
                }

                writer.println();
            }

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
