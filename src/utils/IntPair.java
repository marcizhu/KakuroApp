package src.utils;

/**
 * IntPair.
 * Subclass of Pair that implements the Comparable interface to be able to use it in Collections considering two pairs with equivalent Integer components to be equal.
 *
 * @version 0.1.0 (20/11/2020)
 */
public class IntPair extends Pair<Integer, Integer> implements Comparable {
    /**
     * Constructor
     *
     * @param o  First element of the pair
     * @param o2 Second element of the pair
     */
    public IntPair(Integer o, Integer o2) {
        super(o, o2);
    }

    /**
     * Comparison operator
     * @param o Object to compare with
     * @return whether the compared objects are smaller, equal or greater. Zero (equal) if o is not an IntPair
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof IntPair)) return 0;
        if (equals(o)) return 0;
        if (this.first.equals(((IntPair) o).first)) {
            if (this.second < ((IntPair)o).second) return -1;
            return 1;
        }
        if (this.first < ((IntPair)o).first) return -1;
        return 1;
    }

    /**
     * Equals operator
     * @param o Object to compare with
     * @return whether the compared objects are equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntPair)) return false;
        return this.first.equals(((IntPair) o).first) && this.second.equals(((IntPair) o).second);
    }
}
