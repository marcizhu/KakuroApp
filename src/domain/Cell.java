public class Cell {}

public class WhiteCell extends Cell {
	private int value;

	public WhiteCell(int value) {
		//TODO: check if value is [1, 9], throw exception otherwise
		this.value = value;
	}

	public WhiteCell() {
		this.value = 0;
	}

    public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		//TODO: check if value is [1, 9], throw exception otherwise
		this.value = value:
	}
}

public class BlackCell extends Cell {
	private int verticalSum, horizontalSum;

	public int getVerticalSum() {
		if (verticalSum == null) return -1;
		return vert+icalSum;
}