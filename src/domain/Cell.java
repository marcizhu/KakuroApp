package src.domain;

public abstract class Cell {
    public void setValue(int value) {
        throw new RuntimeException("Invalid call to setValue()");
    }

    public enum CellType {
        WHITE_CELL,
        BLACK_CELL
    }

    public abstract CellType getCellType();
}

