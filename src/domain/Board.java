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

        cells = new Cell[height][width];

        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++) {
                if(b.cells[i][j] instanceof BlackCell) {
                    int r = ((BlackCell) b.cells[i][j]).getHorizontalSum();
                    int c = ((BlackCell) b.cells[i][j]).getVerticalSum();
                    cells[i][j] = new BlackCell(c, r);
                } else {
                    int v = b.cells[i][j].getValue();
                    cells[i][j] = new WhiteCell(v);
                }
            }
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

    public int getValue(int row, int col) {
        return this.cells[row][col].getValue();
    }

    public void clearCellValue(int row, int col) {
        this.cells[row][col].clearValue();
    }

    public boolean isEmpty(int row, int col) {
        return this.cells[row][col].isEmpty();
    }

    public boolean isBlackCell(int row, int col) {
        return this.cells[row][col] instanceof BlackCell;
    }

    public boolean isWhiteCell(int row, int col) {
        return this.cells[row][col] instanceof WhiteCell;
    }

    public void setCell(Cell cell, int row, int col) {
        // TODO: handle out of bounds exception
        cells[row][col] = cell;
    }
}