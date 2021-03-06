package src.domain.entities;

import java.util.UUID;

/**
 * Class that represents a Kakuro board
 *
 * @version 0.1.0 (17/11/2020)
 */

public class Board {
    private final UUID id;
    private final int width;
    private final int height;
    private final Cell[][] cells;


    /**
     * Creates a board with dimensions width x height.
     * @param width  The width of the board
     * @param height The height of the board
     */
    public Board(int width, int height) {
        this.id = UUID.randomUUID();
        this.width = width;
        this.height = height;
        cells = new Cell[height][width]; // Reminder: cells is declared but no cells are created.
    }

    /**
     * Creates a board with dimensions width x height and a given id.
     * @param id  The board's unique identifier
     * @param width  The width of the board
     * @param height The height of the board
     */
    public Board(UUID id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        cells = new Cell[height][width]; // Reminder: cells is declared but no cells are created.
    }

    /**
     * Constructor.
     * Initializes a board with the given size and all cells set to the given one
     * @param width  The width of the board
     * @param height The height of the board
     * @param c      Cell to fill the board
     */
    public Board(int width, int height, Cell c) {
        this.id = UUID.randomUUID();
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];

        boolean isWhite = c instanceof WhiteCell;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                cells[i][j] = (isWhite ? new WhiteCell((WhiteCell)c) : new BlackCell((BlackCell)c));
                cells[i][j].setCoordinates(i, j);
            }
        }
    }

    /**
     * Constructor.
     * Initializes a board with all its parameters, used for deserialization
     * @param id  The id of the board
     * @param width  The width of the board
     * @param height The height of the board
     * @param cells  Matrix of cells to fill the board
     */
    public Board(UUID id, int width, int height, Cell[][] cells) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    /**
     * Copy-constructor
     * @param b Board to copy
     */
    public Board(Board b) {
        this.id = UUID.randomUUID();
        width = b.getWidth();
        height = b.getHeight();
        cells = new Cell[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (b.cells[i][j] instanceof BlackCell) {
                    BlackCell c = b.cells[i][j] == null ? null : new BlackCell((BlackCell) b.cells[i][j]);
                    cells[i][j] = c;
                } else {
                    WhiteCell c = b.cells[i][j] == null ? null : new WhiteCell((WhiteCell) b.cells[i][j]);
                    cells[i][j] = c;
                }
                if (cells[i][j] != null) cells[i][j].setCoordinates(i, j);
            }
        }
    }

    /**
     * Get id of the board
     * @return unique identifier of the board
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get width of the board
     * @return the width of the board
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get height of the board
     * @return the height of the board
     */
    public int getHeight() {
        return height;
    }

    /**
     * Check whether the cell at (i, j) is equal to the given cell
     * @param row  Row of the cell to check
     * @param col  Column of the cell to check
     * @param cell Cell to compare against
     * @return whether the given cell was equal to the cell at (i, j) or not
     */
    public boolean equalsCell(int row, int col, Cell cell) { // checks if they are the same object instance
        return cells[row][col] == cell;
    }

    /**
     * Get cell
     * @param row Row of the cell to get
     * @param col Column of the cell to get
     * @return the requested cell
     */
    public Cell getCell(int row, int col) { return cells[row][col]; }

    /**
     * Set value of cell
     * @param row   Row of the cell to set the value
     * @param col   Column of the cell to set the value
     * @param value Value to set this cell to
     */
    public void setCellValue(int row, int col, int value) {
        cells[row][col].setValue(value);
    }

    /**
     * Get value of a (white) cell
     * @param row Row of the cell to get the value
     * @param col Column of the cell to get the value
     * @return The value of the cell at (row, col)
     */
    public int getValue(int row, int col) {
        return cells[row][col].getValue();
    }

    /**
     * Set notations for the given cell
     * @param row     Row of the cell to set the notations
     * @param col     Column of the cell to set the notations
     * @param value   Value of the notation to set
     * @param checked Whether this notation will be set or unset
     */
    public void setCellNotation(int row, int col, int value, boolean checked) {
        cells[row][col].setNotation(value, checked);
    }

    /**
     * Get cell notations
     * @param row Row of the cell from which to get the notations
     * @param col Column of the cell from which to get the notations
     * @return the notations of the requested cell
     */
    public int getCellNotations(int row, int col) {
        return cells[row][col].getNotations();
    }

    /**
     * Check whether a cell has a checked notation or not
     * @param row      Row of the cell to check
     * @param col      Column of the cell to check
     * @param notation Notation to check (in the range [1, 9])
     * @return whether this notation is set or unset for the requested cell
     */
    public boolean cellHasNotation(int row, int col, int notation) {
        return cells[row][col].isNotationChecked(notation);
    }

    /**
     * Get number of notations on a given cell
     * @param row Row of the cell
     * @param col Column of the cell
     * @return the amount of notations set on the given cell (in the range [0, 9])
     */
    public int getCellNotationSize(int row, int col) {
        return cells[row][col].getNotationSize();
    }

    /**
     * Clear all cell notations for a given cell
     * @param row Row of the cell to clear
     * @param col Column of the cell to clear
     */
    public void clearCellNotations(int row, int col) {
        cells[row][col].clearAllNotations();
    }

    /**
     * Get horizontal sum of a (black) cell
     * @param row Row of the cell to get the horizontal sum
     * @param col Column of the cell to get the horizontal sum
     * @return The horizontal sum indicated by the cell at (row, col)
     */
    public int getHorizontalSum(int row, int col) {
        return cells[row][col].getHorizontalSum();
    }

    /**
     * Get vertical sum of a (black) cell
     * @param row Row of the cell to get the vertical sum
     * @param col Column of the cell to get the vertical sum
     * @return The vertical sum indicated by the cell at (row, col)
     */
    public int getVerticalSum(int row, int col) {
        return cells[row][col].getVerticalSum();
    }

    /**
     * Clear the value of a (white) cell
     * @param row Row of the cell to clear
     * @param col Column of the cell to clear
     */
    public void clearCellValue(int row, int col) {
        cells[row][col].clearValue();
    }

    /**
     * Check whether a cell is empty or no
     * @param row Row of the cell to check
     * @param col Column of the cell to check
     * @return `true` if the cell at (row, col) is empty, `false` otherwise
     */
    public boolean isEmpty(int row, int col) {
        return cells[row][col].isEmpty();
    }

    /**
     * Check whether a cell is black or not
     * @param row Row of the cell to check
     * @param col Column of the cell to check
     * @return `true` if the cell at (row, col) is black, `false` otherwise
     */
    public boolean isBlackCell(int row, int col) {
        return cells[row][col] instanceof BlackCell;
    }

    /**
     * Check whether a cell is white or not
     * @param row Row of the cell to check
     * @param col Column of the cell to check
     * @return `true` if the cell at (row, col) is white, `false` otherwise
     */
    public boolean isWhiteCell(int row, int col) {
        return cells[row][col] instanceof WhiteCell;
    }

    /**
     * Set a cell of the board
     * @param cell Cell object to set
     * @param row  Row of the cell to set
     * @param col  Column of the cell to set
     */
    public void setCell(Cell cell, int row, int col) {
        if(row >= height || col >= width || row < 0 || col < 0)
            throw new IndexOutOfBoundsException("Invalid row/column");

        cells[row][col] = cell; // Reminder: this assigns the same instance cell to cells[row][col]. NOT a copy
        cells[row][col].setCoordinates(row, col);
    }

    /**
     * Converts a board to string
     * @return A string that represents this board
     */
    public String toString() {
        String[] row = new String[height];
        String[] col = new String[width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                String c = cells[i][j] == null ? "x" : cells[i][j].toString();
                col[j] = c;
            }

            row[i] = String.join(",", col);
        }

        String header = height + "," + width + "\n";
        return header + String.join("\n", row);
    }

    /**
     * Converts a board's cells to string
     * @return A string that represents this board's cells
     */
    public String cellsToString() {
        String[] row = new String[height];
        String[] col = new String[width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                String c = cells[i][j] == null ? "x" : cells[i][j].toString();
                col[j] = c;
            }

            row[i] = String.join(",", col);
        }

        return  String.join("\n", row);
    }

    /**
     * Converts a board's notations to string
     * @return A string that represents this board's notations
     */
    public String notationsToString() {
        String[] row = new String[height];
        String[] col = new String[width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (cells[i][j] == null) col[j] = "x";
                else if (cells[i][j] instanceof BlackCell) col[j] = "*";
                else col[j] = ""+cells[i][j].getNotations();
            }
            row[i] = String.join("", col);
        }

        return String.join("", row);
    }
}
