package src.domain.entities;

import src.utils.Pair;

public class Movement {
    private final int index;
    private final int previous;
    private final int next;
    private final int rowIdx;
    private final int colIdx;

    public Movement(int index, int previous, int next, int r, int c) {
        this.index = index;
        this.previous = previous;
        this.next = next;
        this.rowIdx = r;
        this.colIdx = c;
    }

    public int getIndex() {
        return this.index;
    }

    public int getPrevious() {
        return this.previous;
    }

    public int getNext() {
        return this.next;
    }

    public Pair<Integer, Integer> getCoordinates() { return new Pair<>(rowIdx, colIdx); }
}
