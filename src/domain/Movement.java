package src.domain;

public class Movement {
    private int index, previous, next;

    public Movement(int index, int previous, int next) {
        this.index = index;
        this.previous = previous;
        this.next = next;
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
}
