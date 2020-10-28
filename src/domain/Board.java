public class Board {
	private int width;
	private int height;
	private ArrayList<ArrayList<>Cell> cells;

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

    public Pair<Integer, Integer> getNextPos(int row, int col) throws IndexOutOfBoundsException, EndOfBoardException {
        if (row >= height || col >= width || row < 0 || col < 0) throw new IndexOutOfBoundsException(“TODO”);
        if (col + 1 == width) {
            row++;
            col = 0;
        }
        else col++;

        if (row >= height) throw new EndOfBoardException();
            
        return Pair.with(row, col);
    }
}