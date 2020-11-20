package src.utils;

/**
 * Pair.
 * Java equivalent to C++'s std::pair
 *
 * @version 0.1.0 (20/11/2020)
 */

public class Pair<T, K> {
    public T first;
    public K second;

    /**
     * Constructor
     * @param t First element of the pair
     * @param k Second element of the pair
     */
    public Pair (T t, K k) {
        first = t;
        second = k;
    }
}
