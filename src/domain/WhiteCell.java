package src.domain;

public class WhiteCell extends Cell {
    private int value;

    public WhiteCell(int value) {
        setValue(value);
    }

    public WhiteCell() {
        this.value = 0;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) throws IllegalArgumentException {
        if (value > 9 || value < 0) // NOTE: 0 is a valid number, represents an empty white cell
            throw new IllegalArgumentException("Value is out of range");

        this.value = value;
    }

    public void clearValue() {
        this.value = 0;
    }

    public boolean isEmpty() {
        return this.value == 0;
    }
}
