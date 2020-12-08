package src.domain.entities;

import src.utils.Pair;

public class Movement {
    private int index, previous, next, rowIdx, colIdx;

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
