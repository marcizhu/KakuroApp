package src.domain;

import src.utils.Pair;

/**
 * Class that represents a Kakuro cell
 *
 * @version 0.1.0 (17/11/2020)
 */

public abstract class Cell {
    // any Cell assigned to a board should have these values assigned to the corresponding position
    private int coordRow, coordCol;

    /**
     * Set coordinated of this cell
     * @param r Row coordinated of this cell
     * @param c Column coordinated of this cell
     */
    public void setCoordinates(int r, int c) {
        this.coordRow = r;
        this.coordCol = c;
    }

    public Pair<Integer, Integer> getCoordinates() {
        return new Pair<>(coordRow, coordCol);
    }

    /**
     * Set value of the cell
     * @param value Value to set
     */
    public void setValue(int value) {
        throw new RuntimeException("Invalid call to setValue()");
    }

    /**
     * Get value of the cell
     * @return the value of the cell
     */
    public int getValue() {
        throw new RuntimeException("Invalid call to getValue()");
    }

    /**
     * Clear value of the cell
     */
    public void clearValue() {
        throw new RuntimeException("Invalid call to clearValue()");
    }

    /**
     * Set notation for this cell
     * @param value   Value of the notation to set
     * @param checked Whether this notation will be set or unset
     */
    public void setNotation(int value, boolean checked) { throw new RuntimeException("Invalid call to clearValue()"); }

    /**
     * Clear all notations for this cell
     */
    public void clearAllNotations() { throw new RuntimeException("Invalid call to clearValue()"); }

    /**
     * Get notations of this cell
     * @return the notations of this cell
     */
    public boolean[] getNotations() { throw new RuntimeException("Invalid call to clearValue()"); }

    /**
     * Get the number of notations of this cell
     * @return the number of notations of this cell (in the range [0, 9])
     */
    public int getNotationSize() { throw new RuntimeException("Invalid call to clearValue()"); }

    /**
     * Check whether a notation is set for this cell
     * @param value Value of the notation to check
     * @return whether the requested notation is set or not
     */
    public boolean isNotationChecked(int value) { throw new RuntimeException("Invalid call to clearValue()"); }

    /**
     * Get horizontal sum indicated by this cell
     * @return 0 if no sum is indicated, a number from 1 to 45 otherwise
     */
    public int getHorizontalSum() {
        throw new RuntimeException("Invalid call to getHorizontalSum()");
    }

    /**
     * Get vertical sum indicated by this cell
     * @return 0 if no sum is indicated, a number from 1 to 45 otherwise
     */
    public int getVerticalSum() {
        throw new RuntimeException("Invalid call to getHorizontalSum()");
    }

    /**
     * Check whether this cell is empty or not
     * @return whether this cell is empty
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * Convert cell to string
     * @return a string that represents this cell
     */
    public abstract String toString();
}
