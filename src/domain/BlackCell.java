package src.domain;

/**
 * Class that represents a black cell
 *
 * @version 0.1.0 (17/11/2020)
 */

public class BlackCell extends Cell {
    private final int verticalSum;
    private final int horizontalSum;

    /**
     * Default constructor.
     * Initializes a black cell with no horizontal or vertical sum
     */
    public BlackCell() {
        verticalSum = horizontalSum = 0;
    }

    /**
     * Copy constructor.
     * @param c Black cell to copy
     */
    public BlackCell(BlackCell c) {
        this.verticalSum = c.verticalSum;
        this.horizontalSum = c.horizontalSum;
    }

    /**
     * Constructor. Initializes a black cell with given horizontal and vertical sums
     * @param vert  Vertical sum indicated by this black cell
     * @param horiz Horizontal sum indicated by this black cell
     */
    public BlackCell(int vert, int horiz) {
        verticalSum = vert;
        horizontalSum = horiz;
    }

    /**
     * Get vertical sum indicated by this cell
     * @return 0 if no sum is indicated, a number from 1 to 45 otherwise
     */
    public int getVerticalSum() {
        return verticalSum;
    }

    /**
     * Get horizontal sum indicated by this cell
     * @return 0 if no sum is indicated, a number from 1 to 45 otherwise
     */
    public int getHorizontalSum() {
        return horizontalSum;
    }

    /**
     * Convert cell to string
     * @return a string that represents this cell
     */
    public String toString() {
        if (verticalSum == 0 && horizontalSum == 0) return "*";

        String ret = "";
        if (verticalSum   > 0) ret += "C" + verticalSum;
        if (horizontalSum > 0) ret += "F" + horizontalSum;

        return ret;
    }
}
