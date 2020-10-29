package src.domain;

public class BlackCell extends Cell {
    private final int verticalSum;
    private final int horizontalSum;

    public BlackCell () {
        verticalSum = horizontalSum = 0;
    }

    public BlackCell(int vert, int horiz) {
        this.verticalSum = vert;
        this.horizontalSum = horiz;
    }

    public int getVerticalSum() {
        return verticalSum;
    }

    public int getHorizontalSum() {
        return horizontalSum;
    }
}
