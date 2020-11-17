package src.domain;

/**
 * Class that represents a white cell
 *
 * @version 0.1.0 (17/11/2020)
 */

public class WhiteCell extends Cell {
    private int value;

    /**
     * Constructor.
     * Initializes a white cell to the given value
     * @param value Value to set the cell to
     */
    public WhiteCell(int value) {
        setValue(value);
    }

    /**
     * Default constructor.
     * Initializes an empty white cell
     */
    public WhiteCell() {
        value = 0;
    }

    /**
     * Get value of the cell
     * @return the value of the cell
     */
    public int getValue() {
        return value;
    }

    /**
     * Set value of the cell
     * @param value Value to set
     */
    public void setValue(int value) throws IllegalArgumentException {
        if (value > 9 || value < 1)
            throw new IllegalArgumentException("Value is out of range");

        this.value = value;
    }

    /**
     * Clear value of the cell
     */
    public void clearValue() {
        value = 0;
    }

    /**
     * Check whether this cell is empty
     * @return
     */
    public boolean isEmpty() {
        return value == 0;
    }

    /**
     * Convert cell to string
     * @return a string that represents this cell
     */
    public String toString() {
        return (value == 0 ? "?" : Integer.toString(value));
    }
}
