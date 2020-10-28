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
    }

	public Board(Board b) {
        this.width = b.getWidth();
        this.height = b.getHeight();
        cells = new Cell[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //cells[i][j] = new Cell(b.getCell(i, j)); //TODO: Make sure it returns a new White or Black cell accordingly
            }
        }
    }

    public Board(String formattedBoard) {
        //TODO:
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
}