package src.domain;

public class WhiteCell extends Cell {
    private int value;

    public WhiteCell(int value) {
        setValue(value);
    }

    public WhiteCell() {
        value = 0;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) throws IllegalArgumentException {
        if (value > 9 || value < 1)
            throw new IllegalArgumentException("Value is out of range");

        this.value = value;
    }

    public void clearValue() {
        value = 0;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public String toString() {
        return (value == 0 ? "?" : Integer.toString(value));
    }
}
