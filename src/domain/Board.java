package src.domain;

import java.util.ArrayList;

public class Board {
	private int width;
	private int height;
	private ArrayList<ArrayList<Cell>> cells;

	public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getCell(int row, int col) {
        return cells.get(row).get(col);
    }

    public void setCellValue(int row, int col, int value) {
        this.cells.get(row).get(col).setValue(value);
    }
}