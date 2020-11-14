package src.domain;

/**
 * Class that represents a white cell
 *
 * @version 0.1.0 (17/11/2020)
 */

public class WhiteCell extends Cell {
    private int value;
    private boolean[] notations;

    /**
     * Default constructor.
     * Initializes an empty white cell
     */
    public WhiteCell() { //Default constructor --> empty value and empty notations
        value = 0;
        initAllNotations(false);
    }

    /**
     * Constructor.
     * Initializes a white cell to the given value
     * @param value Value to set the cell to
     */
    public WhiteCell(int value) {
        setValue(value);
        initAllNotations(false);
    }

    // TODO: add javadoc
    public WhiteCell(boolean defaultNotation) {
        value = 0;
        initAllNotations(defaultNotation);
    }

    // TODO: add javadoc
    public WhiteCell(int value, boolean defaultNotation) {
        setValue(value);
        initAllNotations(defaultNotation);
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

    // TODO: add javadoc
    public boolean[] getNotations() {
        return notations;
    }

    // TODO: add javadoc
    public boolean isNotationChecked(int value) {
        if (value > 9 || value < 1)
            throw new IllegalArgumentException("Value is out of range");
        return notations[value-1];
    }

    // TODO: add javadoc
    public void setNotation(int value, boolean checked) {
        if (value > 9 || value < 1)
            throw new IllegalArgumentException("Value is out of range");
        notations[value-1] = checked;
    }

    // TODO: add javadoc
    public void clearAllNotations() {
        for (int i = 0; i < 9; i++) notations[i] = false;
    }
    
    /**
     * Clear value of the cell
     */
    public void clearValue() {
        value = 0;
    }

    /**
     * Check whether this cell is empty or not
     * @return whether this cell is empty
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

    private void initAllNotations(boolean b) {
        notations = new boolean[] { b, b, b, b, b, b, b, b, b };
    }
}
