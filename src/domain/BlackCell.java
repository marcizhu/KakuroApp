package src.domain;

public class BlackCell extends Cell {
    private final int verticalSum;
    private final int horizontalSum;

    public BlackCell(int vert, int horiz) {
        this.verticalSum = vert;
        this.horizontalSum = horiz;
    }

    public int getVerticalSum() {
        return (verticalSum == 0 ? -1 : verticalSum);
    }

    public int getHorizontalSum() {
        return (horizontalSum == 0 ? -1 : horizontalSum);
    }

    @Override
    public CellType getCellType() {
        return CellType.BLACK_CELL;
    }
}
