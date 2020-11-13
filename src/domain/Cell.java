package src.domain;

public class Cell {
    public void setValue(int value) {
        throw new RuntimeException("Invalid call to setValue()");
    }

    public int getValue() {
        throw new RuntimeException("Invalid call to getValue()");
    }

    public void clearValue() {
        throw new RuntimeException("Invalid call to clearValue()");
    }

    public int getHorizontalSum() {
        throw new RuntimeException("Invalid call to getHorizontalSum()");
    }

    public int getVerticalSum() {
        throw new RuntimeException("Invalid call to getHorizontalSum()");
    }

    public boolean isEmpty() {
        return false;
    }

    public String toString() {
        return "";
    }
}
