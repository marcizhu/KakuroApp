package src.domain;



public class Board {
	private int width;
	private int height;
	private Cell[][] cells;

	public Board() {
        width = 0;
        height = 0;
    }

    public Board(int width, int height) {
        this.width = width;
        this.height = height;

        cells = new Cell[height][width];
    }

	public Board(Board b) {
        this.width = b.getWidth();
        this.height = b.getHeight();
        cells = b.cells;
    }

    public Board(String formattedBoard) {
        // TODO: Implement this
    }

	public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public void setCellValue(int row, int col, int value) {
        this.cells[row][col].setValue(value);
    }

    public void setCell(Cell cell, int row, int col) {
	    //TODO: handle out of bounds exception
        cells[row][col] = cell;
    }
}