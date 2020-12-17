package src.domain.entities;

/**
 * Class that represents a white cell
 *
 * @version 0.1.0 (17/11/2020)
 */

public class WhiteCell extends Cell {
    private int value;
    private int notations;

    /**
     * Default constructor.
     * Initializes an empty white cell
     */
    public WhiteCell() { //Default constructor --> empty value and empty notations
        value = 0;
        initAllNotations(false);
    }

    /**
     * Copy constructor
     * @param c White cell to copy
     */
    public WhiteCell(WhiteCell c) {
        this.value = c.value;
        this.notations = c.notations;
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

    /**
     * Constructor
     * Initializes a white cell as empty with default notations set or unset
     * @param defaultNotation Whether all notations are set or unset
     */
    public WhiteCell(boolean defaultNotation) {
        value = 0;
        initAllNotations(defaultNotation);
    }

    /**
     * Constructor
     * Initializes a white cell to the given value with default notations set or unset
     * @param value           Value to set the cell to
     * @param defaultNotation Whether all notations are set or unset
     */
    public WhiteCell(int value, boolean defaultNotation) {
        setValue(value);
        initAllNotations(defaultNotation);
    }

    /**
     * Constructor
     * Initializes a white cell with all its attributes (used by CellDeserializer)
     * @param row Vertical position of the cell relative to the board
     * @param col Horizontal position of the cell relative to the board
     * @param value Value to set the cell to
     * @param notations the value for the notations
     */
    public WhiteCell(int row, int col, int value, int notations) {
        this.value = value;
        this.notations = notations;
        this.setCoordinates(row, col);
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
     * Get notations of this cell
     * @return the notations of this cell
     */
    public int getNotations() {
        return notations;
        /*boolean[] ret = new boolean[9];

        for(int i = 0; i < 9; i++)
            ret[i] = ((notations & (1 << i)) != 0);

        return ret;*/
    }

    /**
     * Get the number of notations of this cell
     * @return the number of notations of this cell (in the range [0, 9])
     */
    public int getNotationSize() {
        return Integer.bitCount(notations);
    }

    /**
     * Check whether a notation is set for this cell
     * @param value Value of the notation to check
     * @return whether the requested notation is set or not
     */
    public boolean isNotationChecked(int value) {
        if (value > 9 || value < 1)
            throw new IllegalArgumentException("Value is out of range");

        return (notations & (1 << (value - 1))) != 0;
    }

    /**
     * Set notation for this cell
     * @param value   Value of the notation to set
     * @param checked Whether this notation will be set or unset
     */
    public void setNotation(int value, boolean checked) {
        if (value > 9 || value < 1)
            throw new IllegalArgumentException("Value is out of range");

        int mask = 1 << (value - 1);
        int flag = checked ? 1 : 0;

        notations ^= (-flag ^ notations) & mask;
    }

    /**
     * Clear all notations for this cell
     */
    public void clearAllNotations() {
        notations = 0;
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
        notations = b ? 0b111111111 : 0b000000000;
    }
}
